package com.example.nutritionalapplication.service

import com.example.nutritionalapplication.model.Food
import retrofit2.http.GET

interface FoodApi {

    // BaseUrl =>  https://raw.githubusercontent.com/
    //EndPoint =>  atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json

    @GET("atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json")
    suspend fun getFood() : List<Food>


}