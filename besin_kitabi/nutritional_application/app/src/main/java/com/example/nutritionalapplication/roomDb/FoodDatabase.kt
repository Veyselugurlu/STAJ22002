package com.example.nutritionalapplication.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutritionalapplication.model.Food

@Database(entities = [Food::class], version = 1)
abstract class FoodDatabase : RoomDatabase(){ //abstract clasın room oldugunu belirtiyoruz
    abstract fun foodDao(): FoodDAO

    //Data Race
    companion object{
        @Volatile
        private var instance : FoodDatabase?= null

        private val lock = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: databaseCreate(context).also {
                instance = it
            }
        }

        private fun databaseCreate(context: Context) = Room.databaseBuilder(
            context.applicationContext ,
            FoodDatabase::class.java,
            "FoodDatabase"
        ).build()
    }
}