package com.thepanshu.mirrorotp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thepanshu.mirrorotp.models.Sms

@Database(entities = [Sms::class], version = 1)
abstract class SmsDatabase: RoomDatabase() {
    abstract fun smsDao(): SmsDao
}