package com.thepanshu.mirrorotp.ui.addDevice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.thepanshu.mirrorotp.R
import com.thepanshu.mirrorotp.ui.authenticate.SplashFragment

class AddDeviceFragment : Fragment() {

    companion object {
        fun newInstance() = AddDeviceFragment()
    }

    private val index = MutableLiveData<Int>()
    private val titles = arrayOf(
        "Download\nMirror OTP\nWeb Extension",
        "Open Mirror OTP\nIn Browser",
        "Scan QR Code\nusing Mirror OTP App",
        "This is it!")
    private val subtitles = arrayOf(
        "Visit Chrome Web store",
        "Visit www.mirrorotp.com",
        "Click SCAN to proceed",
        "OTPs when received will be shown in your browser extension."
    )
    private val buttonTitles = arrayOf("Next", "Next", "Scan","Finish")
    private val images = arrayOf(R.drawable.download_ext, R.drawable.browser, R.drawable.scan, R.drawable.ready)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_device, container, false)
        val titleTextView = root.findViewById<TextView>(R.id.title)
        val subtitleTextView = root.findViewById<TextView>(R.id.subtitle)
        val backButton: ImageButton = root.findViewById(R.id.backButton)
        val nextButton: MaterialButton = root.findViewById(R.id.nextButton)
        val imageView: ImageView = root.findViewById(R.id.connectImage)
        nextButton.setOnClickListener {
            if(index.value == 3) {
                scanBarcode()
            }
            else {
                index.value = index.value!!+1
            }
        }
        index.observe(viewLifecycleOwner, {
            when (it) {
                1 -> {
                    titleTextView.text = titles[0]
                    subtitleTextView.text = subtitles[0]
                    imageView.setImageResource(images[0])
                    nextButton.text = buttonTitles[0]
                }
                2 -> {
                    titleTextView.text = titles[1]
                    subtitleTextView.text = subtitles[1]
                    imageView.setImageResource(images[1])
                    nextButton.text = buttonTitles[1]
                }
                3 -> {
                    titleTextView.text = titles[2]
                    subtitleTextView.text = subtitles[2]
                    imageView.setImageResource(images[2])
                    nextButton.text = buttonTitles[2]
                    // ask for permission
                }
                else -> {
                    // TODO: Go Back to main
                }
            }
        })
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        index.value = 1

    }
    private fun scanBarcode() {

        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.add_device_frag_container, ScanFragment.newInstance())
            .commit()

    }
}