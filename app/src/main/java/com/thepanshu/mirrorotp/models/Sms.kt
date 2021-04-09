package com.thepanshu.mirrorotp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sms(
    @PrimaryKey(autoGenerate = true) val uid:Long?= null,
    @ColumnInfo(name = "sender_name") val sender: String,
    @ColumnInfo(name = "message_body") val body: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "otp") val otp: Int = 888888
)
