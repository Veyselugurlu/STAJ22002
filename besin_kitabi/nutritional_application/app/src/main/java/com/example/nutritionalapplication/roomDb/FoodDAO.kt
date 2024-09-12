package com.example.nutritionalapplication.roomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nutritionalapplication.model.Food

@Dao
interface FoodDAO {
    @Insert
    suspend fun insertAll(vararg food : Food) : List<Long>
    //ekledigi besinlerin id'sini long olarak veriyor
    @Query("SELECT * FROM food")
    suspend fun getAllFood() : List<Food>

    @Query("SELECT * FROM food WHERE uuid = :foodId")
    suspend fun getFood(foodId : Int) : Food

    @Query("DELETE FROM food")
    suspend fun deleteAllFood()


}