package com.thepanshu.mirrorotp.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    // TODO: SMS Receiver not working initially

    private lateinit var homeViewModel: HomeViewModel
    private var userToken: String? = null
    var list = arrayListOf<Sms>()
    private lateinit var recyclerView: RecyclerView
    private var broadcastReceiver: BroadcastReceiver? = null

    private val db: SmsDatabase by lazy {
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
        val refreshButton = root.findViewById<MaterialButton>(R.id.refreshButton)
        refreshButton.setOnClickListener {
            updateRv()
        }
        updateRv()
        return root
    }

    private fun updateRv() {
        GlobalScope.launch(Dispatchers.IO) {
            // DO something: Progress Bar
        }
        db.smsDao().getAll().observe(viewLifecycleOwner, {
            if (it != null) {
                list = it as ArrayList<Sms>
                recyclerView.adapter = SmsListAdapter(it)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("TOKEN_PREF", Context.MODE_PRIVATE)
        userToken = sharedPref?.getString("USER_BACKEND_AUTH_TOKEN", null)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}