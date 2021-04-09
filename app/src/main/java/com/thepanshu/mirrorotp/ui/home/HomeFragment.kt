package com.thepanshu.mirrorotp.ui.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.button.MaterialButton
import com.thepanshu.mirrorotp.R
import com.thepanshu.mirrorotp.adapters.SmsListAdapter
import com.thepanshu.mirrorotp.database.SmsDatabase
import com.thepanshu.mirrorotp.models.Sms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var userToken: String? = null
    var list = arrayListOf<Sms>()
    private lateinit var recyclerView: RecyclerView

    val db: SmsDatabase by lazy {
        Room.databaseBuilder(
            requireContext(),
            SmsDatabase::class.java,
            "sms.db"
        )
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val addDeviceButton: MaterialButton = root.findViewById(R.id.addDeviceButton)
        addDeviceButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addDeviceFragment)
        }
        recyclerView = root.findViewById(R.id.smsRv)
        recyclerView.adapter = SmsListAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        updateRv()
        return root
    }

    private fun updateRv() {
        db.smsDao().getAll().observe(viewLifecycleOwner, {
            if(it!=null) {
                list = it as ArrayList<Sms>
                recyclerView.adapter = SmsListAdapter(it)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("TOKEN_PREF", Context.MODE_PRIVATE)
        userToken = sharedPref?.getString("USER_BACKEND_AUTH_TOKEN", null)

        if(checkSelfPermission(requireContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 111)
        }
        else {
            receiveMessage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            receiveMessage()
        }
    }

    private fun receiveMessage() {
        // TODO: Use background service
        var broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                // TODO: Extract OTP
                for(sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                    val receivedSms = Sms(
                        sender = sms.displayOriginatingAddress,
                        body = sms.displayMessageBody,
                        date = sms.timestampMillis
                    )
                    GlobalScope.launch(Dispatchers.IO) {
                        db.smsDao().insertSms(receivedSms)
                        launch(Dispatchers.Main) {
                            updateRv()
                            Toast.makeText(requireContext(), sms.displayOriginatingAddress + " " + sms.displayMessageBody, Toast.LENGTH_LONG).show()
                        }
                    }

                    // TODO: Post using work manager to Socket
                    // TODO: Animate recycler view UI

                }
            }

        }

        activity?.registerReceiver(broadcastReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
}