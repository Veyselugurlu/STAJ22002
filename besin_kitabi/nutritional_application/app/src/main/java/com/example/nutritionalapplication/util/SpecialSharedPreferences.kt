package com.example.nutritionalapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.nutritionalapplication.roomDb.FoodDatabase

class SpecialSharedPreferences {

    companion object{
        private val TIME = "time"
        private var sharedPreferences : SharedPreferences?= null

        @Volatile
        private var instance : SpecialSharedPreferences?= null

        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: specialSharedPreferencesCreate(context).also {
                instance = it
            }
        }
        private fun specialSharedPreferencesCreate(context: Context): SpecialSharedPreferences{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return SpecialSharedPreferences()
        }
    }

    fun timeRegister(zaman: Long){
        sharedPreferences?.edit()?.putLong(TIME,zaman)?.apply()
    }

    fun getTime() = sharedPreferences?.getLong(TIME,0)
}