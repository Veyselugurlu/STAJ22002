package com.example.yemeksiparis.data

import com.example.yemeksiparis.model.Yemekler

class YRepo(var yds : YDataSource) {
    suspend fun yemekleriYukle(): List<Yemekler> = yds.yemekleriYukle()
}