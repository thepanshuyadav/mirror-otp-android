package com.thepanshu.mirrorotp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.green
import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.thepanshu.mirrorotp.MainActivity
import com.thepanshu.mirrorotp.R

class PermissionsFragment : Fragment() {
    // TODO: Fix this
    private lateinit var cameraPermissionButton: MaterialButton
    private lateinit var smsPermissionButton: MaterialButton
    private lateinit var continueButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_permissions, container, false)

        continueButton = root.findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            checkAllGranted()
        }
        cameraPermissionButton = root.findViewById(R.id.cameraPermissionButton)
        smsPermissionButton = root.findViewById(R.id.smsPermissionButton)
        // Check if already granted
        cameraPermissionButton.setOnClickListener {
            if(checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        333
                )
            }
            else {
                Snackbar.make(requireView(), "Permission Given!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.GREEN)
                        .show()
            }
        }
        smsPermissionButton.setOnClickListener {
            if(checkSelfPermission(requireContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        arrayOf(Manifest.permission.RECEIVE_SMS),
                        111
                )
            }
            else {
                Snackbar.make(requireView(), "Permission Given!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.GREEN)
                        .show()
            }
        }
        return root
    }

    private fun checkAllGranted() {
        if(checkSelfPermission(requireContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
        else {
            Toast.makeText(requireContext(), "Please grant permission", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAllGranted()
    }

// TODO: Not working, fix
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 333 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            cameraPermissionButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_green_light)
            cameraPermissionButton.text = "Granted"
            Log.d("PER", "Granted")
        }
        else if(requestCode == 111 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            smsPermissionButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_green_light)
            smsPermissionButton.text = "Granted"
            Log.d("PER", "Granted")
        }
        else {
            Snackbar.make(requireView(), "Please grant permission", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show()
        }
    }
    companion object {
        fun newInstance() = PermissionsFragment()
    }
}