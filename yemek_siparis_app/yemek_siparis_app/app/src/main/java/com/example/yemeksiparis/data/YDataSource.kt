package com.example.yemeksiparis.data

import com.example.yemeksiparis.model.YemekCevap
import com.example.yemeksiparis.model.Yemekler
import com.example.yemeksiparis.retrofit.YemeklerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET

class YDataSource(var fdao: YemeklerDao) {
    suspend fun yemekleriYukle(): List<Yemekler> =
        withContext(Dispatchers.IO) {
            return@withContext fdao.yemekleriYukle().yemekler
        }
}
