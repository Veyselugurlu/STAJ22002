package com.example.yemeksiparis.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Url

class RetrofitClient {
    companion object{ //static  clas ismi ile icerisine yazacagimiz fonksiyona erisiriz.

        fun getClient(baseUrl: String)  :Retrofit{
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }
}