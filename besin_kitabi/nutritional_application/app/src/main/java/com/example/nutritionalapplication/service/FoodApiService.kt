package com.example.nutritionalapplication.service

import com.example.nutritionalapplication.model.Food
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodApiService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        //atacagimiz istek sonucunun gson olarak gelecegini soyluyor.
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FoodApi::class.java)

    suspend fun getData() : List<Food>{
        return retrofit.getFood()
    }
}