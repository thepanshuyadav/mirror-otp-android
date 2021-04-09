package com.thepanshu.mirrorotp

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class MainActivity : AppCompatActivity() {

    private var mSocket: Socket? = null
    private var userToken: String? = null
    //TODO: Use LiveData
    private val smsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        userToken = sharedPref.getString("BACKEND_SIGN_IN_TOKEN", null)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 111)
        }
        else {
            receiveMessage()
        }
    }
    private fun receiveMessage() {
        var broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                for(sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                    // TODO: Post using work manager
                    //Toast.makeText(requireContext(), sms.displayOriginatingAddress + " " + sms.displayMessageBody, Toast.LENGTH_LONG).show()

                    smsList.add(sms.toString())
                    Log.d("SMS", smsList.toString())
                }
            }

        }

        registerReceiver(broadcastReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            receiveMessage()
        }
    }
}