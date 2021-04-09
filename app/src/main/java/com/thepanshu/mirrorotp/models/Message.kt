package com.thepanshu.mirrorotp.models

import java.util.*

data class Message(
        val id:Long?= null,
        val messageBody:String,
        val sender:String,
        val date: Date
)
