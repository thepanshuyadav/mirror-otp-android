package com.thepanshu.mirrorotp.ui.dashboard

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.thepanshu.mirrorotp.R


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val profileImage = root.findViewById<ImageView>(R.id.profile_pic_img)
        val emailTextView = root.findViewById<TextView>(R.id.user_email_tv)
        val nameTextView = root.findViewById<TextView>(R.id.username_tv)
//        textView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val acct = GoogleSignIn.getLastSignedInAccount(activity)
        if (acct != null) {
            val personName = acct.displayName
            val personEmail = acct.email
            val personPhoto: Uri? = acct.photoUrl

            Glide.with(requireContext()).load(Uri.parse(personPhoto.toString())).into(profileImage)
            emailTextView.text = personEmail
            nameTextView.text = personName


        }
        Log.d("user", acct.toString())
        return root
    }


}