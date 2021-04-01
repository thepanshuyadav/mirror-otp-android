package com.thepanshu.mirrorotp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.thepanshu.mirrorotp.MainActivity
import com.thepanshu.mirrorotp.R
import com.thepanshu.mirrorotp.models.SignInToken
import com.thepanshu.mirrorotp.retrofit.SignInService
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class SignInFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: MaterialButton

    companion object {
        fun newInstance() = SignInFragment()
        private const val TAG = "SignInFragment"
        private const val RC_SIGN_IN = 9001
        private const val WEB_CLIENT_ID = "458959227295-23ph4893vf9ku2kgge26p2thqf5m9ie0.apps.googleusercontent.com"
    }

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.signin_fragment, container, false)
        signInButton = root.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken

                // Send ID Token to server and validate
                if(idToken!=null) {
                    getAuthToken(SignInToken(idToken))
                }
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun getAuthToken(token: SignInToken) {
        val apiService = SignInService()
        apiService.addUser(token)
        {
            if (it?.userTokenId != null) {
                // it = newly added user parsed as response
                // it?.id = newly added user ID
                Log.d(TAG,"backEndToken: ${it.userTokenId}")
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                sharedPref?.edit()?.putString("BACKEND_SIGN_IN_TOKEN" ,it.userTokenId)?.apply()
                firebaseAuthWithGoogle(token.userTokenId!!)
            } else {
                Log.e(TAG,"Error registering new user")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = auth.currentUser
                updateUI(user, "Signed in successfully")
            }
            else {
                Log.w(TAG, "signInWithCredential:failure", it.exception)
                updateUI(null, it.exception!!.message.toString())
            }
        }
    }

    private fun updateUI(user: FirebaseUser?, message: String) {
        if(user == null) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()

        } else {
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

}