package com.thepanshu.mirrorotp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.thepanshu.mirrorotp.models.Sms

@Dao
interface SmsDao {
    @Query("SELECT * FROM Sms ORDER BY date DESC LIMIT 50")
    fun getAll(): LiveData<List<Sms>>

//    @Delete
//    fun delete(sms: Sms)

    @Insert
    fun insertSms(sms: Sms)
}