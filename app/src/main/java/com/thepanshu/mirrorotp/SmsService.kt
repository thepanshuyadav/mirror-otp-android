package com.thepanshu.mirrorotp

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import androidx.annotation.Nullable
import androidx.room.Room
import com.thepanshu.mirrorotp.database.SmsDatabase
import com.thepanshu.mirrorotp.models.Sms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


class SmsService : Service() {
    var smsReceiver: SmsReceiver = SmsReceiver()
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        return START_STICKY
    }

    inner class SmsReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val db: SmsDatabase by lazy {
                Room.databaseBuilder(
                        p0!!,
                        SmsDatabase::class.java,
                        "sms.db"
                )
                        .build()
            }
            var otp = "xxxxxx"
            val pattern1 = Pattern.compile("(|^)\\d{6}")
            val pattern2 = Pattern.compile("(|^)\\d{7}")
            val pattern3 = Pattern.compile("(|^)\\d{5}");
            for(sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                if (sms.displayMessageBody != null) {
                    val m1: Matcher = pattern1.matcher(sms.displayMessageBody)
                    val m2: Matcher = pattern2.matcher(sms.displayMessageBody)
                    val m3: Matcher = pattern3.matcher(sms.displayMessageBody)
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
                    }

                }
                Log.d("BG", otp)
                val receivedSms = Sms(
                        sender = sms.displayOriginatingAddress,
                        body = sms.displayMessageBody,
                        date = sms.timestampMillis,
                        otp = otp
                )
                GlobalScope.launch(Dispatchers.IO) {
                    // TODO: Post using work manager to Socket
                    db.smsDao().insertSms(receivedSms)
                    Log.d("BG", receivedSms.toString())
                    val intent = Intent("com.thepanshu.RECEIVED_SMS")
//                    val extras = Bundle()
//                    extras.putString("sender", sms.displayOriginatingAddress)
//                    extras.putString("body", sms.displayMessageBody)
//                    extras.putString("otp", otp)
//                    intent.putExtras(extras)
                    launch(Dispatchers.Main) {
                        p0?.sendBroadcast(intent)
                    }


                }

            }
        }
    }
}