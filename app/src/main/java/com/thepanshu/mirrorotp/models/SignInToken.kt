package com.thepanshu.mirrorotp.models

import com.google.gson.annotations.SerializedName

data class SignInToken(
    @SerializedName("userTokenId") val userTokenId: String?
)
