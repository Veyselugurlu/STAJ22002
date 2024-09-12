package com.example.yemekkitabi.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yemekkitabi.model.Tarif

@Database(entities = [Tarif::class], version = 1)
abstract class TarifDatabase : RoomDatabase(){
    abstract fun TarifDao() : TarifDAO

}