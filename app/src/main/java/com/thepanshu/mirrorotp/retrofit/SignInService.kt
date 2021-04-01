package com.thepanshu.mirrorotp.retrofit

import android.util.Log
import com.thepanshu.mirrorotp.models.SignInToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInService {
    fun addUser(signInToken: SignInToken, onResult: (SignInToken?) -> Unit){
        val retrofit = SignInServiceBuilder.buildService(SignInApi::class.java)
        retrofit.postToken(signInToken).enqueue(
            object : Callback<SignInToken> {
                override fun onFailure(call: Call<SignInToken>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse( call: Call<SignInToken>, response: Response<SignInToken>) {
                    val addedUserToken = response.body()
                    if (addedUserToken?.userTokenId != null) {
                        //sessionManager.saveAuthToken(loginResponse.authToken)

                    }
                    onResult(addedUserToken)
                }
            }
        )
    }
}