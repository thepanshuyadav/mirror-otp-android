package com.thepanshu.mirrorotp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import androidx.room.Room
import com.thepanshu.mirrorotp.database.SmsDatabase
import com.thepanshu.mirrorotp.models.Sms
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern


class SmsReceiver : BroadcastReceiver() {
    private var mSocket: Socket? = null
    private var userToken: String? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == p1!!.action
                || Intent.ACTION_BOOT_COMPLETED == p1.action
                || Intent.ACTION_REBOOT == p1.action
                || Intent.ACTION_SHUTDOWN == p1.action) {

            val i = Intent(p0, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            p0!!.startActivity(i)
        }
        if (p1.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle = p1.extras
            if (bundle != null) {
                try {
                    val db: SmsDatabase by lazy {
                        Room.databaseBuilder(
                                p0!!,
                                SmsDatabase::class.java,
                                "sms.db"
                        )
                                .build()
                    }
                    val pdus = bundle.get("pdus") as Array<*>
                    val sms: SmsMessage = SmsMessage.createFromPdu(pdus[0] as ByteArray)
                    Log.d("SMS", sms.toString())
                    var otp = "xxxxxx"
                    val pattern1 = Pattern.compile("(|^)\\d{6}")
                    val pattern2 = Pattern.compile("(|^)\\d{7}")
                    val pattern3 = Pattern.compile("(|^)\\d{5}")
                    val pattern4 = Pattern.compile("(|^)\\d{4}")
                    if (sms.displayMessageBody != null) {
                        val m1: Matcher = pattern1.matcher(sms.displayMessageBody)
                        val m2: Matcher = pattern2.matcher(sms.displayMessageBody)
                        val m3: Matcher = pattern3.matcher(sms.displayMessageBody)
                        val m4: Matcher = pattern4.matcher(sms.displayMessageBody)
                        when {
                            m1.find() -> {
                                otp = m1.group(0)
                            }
                            m2.find() -> {
                                otp = m2.group(0)
                            }
                            m3.find() -> {
                                otp = m3.group(0)
                            }
                            m4.find() -> {
                                otp = m4.group(0)
                            }
                        }

                    }
                    Log.d("SMS", otp)
                    val receivedSms = Sms(
                            sender = sms.displayOriginatingAddress,
                            body = sms.displayMessageBody,
                            date = sms.timestampMillis,
                            otp = otp
                    )
                    GlobalScope.launch(Dispatchers.IO) {
                        // TODO: Post using work manager to Socket
                        db.smsDao().insertSms(receivedSms)
                        db.close()
                        Log.d("SMS", receivedSms.toString())
                        val intent = Intent("com.thepanshu.RECEIVED_SMS")
                        mSocket = IO.socket("https://mirror-otp.herokuapp.com")
                        mSocket!!.connect()
                        val sharedPref = p0!!.getSharedPreferences("TOKEN_PREF", Context.MODE_PRIVATE)
                        if (sharedPref != null) {
                            userToken = sharedPref.getString("USER_BACKEND_AUTH_TOKEN", null)
                        }
                        val smsJSONObject = JSONObject("""{"otp":"${receivedSms.otp}", "time":"${receivedSms.date}", "name":"${receivedSms.sender}"}""")
                        val serverJSONObject = JSONObject("""{"userAuthToken":"${userToken.toString()}"}""")
                        val otpJSONObject = JSONObject("""{"userAuthToken":"${userToken.toString()}", "otpList":[${smsJSONObject}]}""")
                        mSocket!!.emit("JOIN_SERVER", serverJSONObject)
                        Log.d("BG", otpJSONObject.toString())
                        // TODO:
                        mSocket!!.emit("GET_OTP", otpJSONObject)
                        launch(Dispatchers.Main) {
                            p0?.sendBroadcast(intent)
                        }

                    }
                } catch (e: Exception) {
                    Log.d("SMS", "Some Error Occured")
                }
            }
            else {
                Log.d("SMS", "Empty Bundle")
            }
        }
    }
}