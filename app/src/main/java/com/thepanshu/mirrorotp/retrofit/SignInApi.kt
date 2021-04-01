package com.thepanshu.mirrorotp.retrofit

import com.thepanshu.mirrorotp.models.SignInToken
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface SignInApi{

    @POST("/api/auth/login")
    fun postToken(@Body signInToken: SignInToken): Call<SignInToken>
}