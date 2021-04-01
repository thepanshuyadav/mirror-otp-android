package com.thepanshu.mirrorotp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.thepanshu.mirrorotp.ui.main.SignInFragment
import com.thepanshu.mirrorotp.ui.main.SplashFragment
import java.util.*
import kotlin.concurrent.schedule

class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SplashFragment.newInstance())
            .commitNow()
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // TODO: Or token expires
        if(firebaseUser == null) {
            // Sign In Fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SignInFragment.newInstance())
                .commitNow()
        }
        else {
            val i = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}