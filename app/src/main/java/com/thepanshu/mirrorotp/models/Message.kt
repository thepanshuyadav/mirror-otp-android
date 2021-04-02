package com.thepanshu.mirrorotp.models

data class Message(
        val id:Long?= null,
        val messageBody:String,
        val sender:String
)
