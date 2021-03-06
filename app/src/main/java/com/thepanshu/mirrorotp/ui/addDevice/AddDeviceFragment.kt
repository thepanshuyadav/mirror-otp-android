package com.thepanshu.mirrorotp.ui.addDevice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.thepanshu.mirrorotp.R
import com.thepanshu.mirrorotp.ScanActivity
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject


class AddDeviceFragment : Fragment() {
    //private lateinit var qrCode: String
    private lateinit var progressBar: ProgressBar
    private var mSocket: Socket? = null
    private var userToken: String? = null

    companion object {
        fun newInstance() = AddDeviceFragment()
    }

    private val index = MutableLiveData<Int>()
    private val titles = arrayOf(
            "Download\nMirror OTP\nWeb Extension",
            "Open Mirror OTP\nIn Browser",
            "Scan QR Code\nusing Mirror OTP App",
            "This is it!"
    )
    private val subtitles = arrayOf(
            "Visit Chrome Web store",
            "Visit www.mirrorotp.com",
            "Click SCAN to proceed",
            "OTPs when received will be shown in your browser extension."
    )
    private val buttonTitles = arrayOf("Next", "Next", "Scan", "Finish")
    private val images = arrayOf(
            R.drawable.download_ext,
            R.drawable.browser,
            R.drawable.scan,
            R.drawable.ready
    )
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_device, container, false)
        val titleTextView = root.findViewById<TextView>(R.id.title)
        val subtitleTextView = root.findViewById<TextView>(R.id.subtitle)
        progressBar = root.findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        val nextButton: MaterialButton = root.findViewById(R.id.nextButton)
        val imageView: ImageView = root.findViewById(R.id.connectImage)
        nextButton.setOnClickListener {
            if(index.value == 3) {
                if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            333
                    )
                } else {
                    scanBarcode()
                }
            }
            else if(index.value == 4) {
                // Send to socket for response
                findNavController().navigate(R.id.action_addDeviceFragment_to_navigation_home)
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
                4 -> {
                    titleTextView.text = titles[3]
                    subtitleTextView.text = subtitles[3]
                    imageView.setImageResource(images[3])
                    nextButton.text = buttonTitles[3]

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
        val sharedPref = activity?.getSharedPreferences("TOKEN_PREF", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            userToken = sharedPref.getString("USER_BACKEND_AUTH_TOKEN", null)
        }
        index.value = 1

    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 333 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            scanBarcode()
        }
    }
    private fun scanBarcode() {
        // TODO: Start activity for result
        val intent = Intent(requireActivity(), ScanActivity::class.java)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if(data!=null) {
                val qrCode = data.getStringExtra("CODE").toString()
                progressBar.visibility = View.VISIBLE
                requireView().isEnabled = false
                Log.d("SOCKET", userToken.toString())
                mSocket = IO.socket("https://mirror-otp.herokuapp.com")
                mSocket!!.connect()
                val serverJSONObject = JSONObject("""{"userAuthToken":"${userToken.toString()}"}""")
                val extensionJSONObject = JSONObject("""{"userAuthToken":"${userToken.toString()}", "clientId":"${qrCode}"}""")
                Log.d( "SOCKET",serverJSONObject.toString())
                mSocket!!.emit("JOIN_SERVER", serverJSONObject)
                if(userToken!=null) {
                    mSocket!!.emit("JOIN_EXTENSION", extensionJSONObject)
                    Log.d("SOCKET", "success")
                }
                progressBar.visibility = View.INVISIBLE
                index.value = index.value!!+1
            }
            else {
                Snackbar.make(requireView(), "Failed scan. Please try again.", Snackbar.LENGTH_SHORT).show()
            }

        }
    }
}