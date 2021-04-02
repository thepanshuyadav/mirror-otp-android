package com.thepanshu.mirrorotp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thepanshu.mirrorotp.ui.addDevice.AddDeviceFragment

class AddDeviceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        supportFragmentManager.beginTransaction()
            .replace(R.id.add_device_frag_container, AddDeviceFragment.newInstance())
            .commitNow()
    }
}