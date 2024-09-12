package com.example.yemeksiparis.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import com.example.yemeksiparis.model.YemekCevap
import com.example.yemeksiparis.model.Yemekler

interface YemeklerDao {

    @GET("yemekler/tumYemekleriGetir.php")
    suspend fun yemekleriYukle() : YemekCevap

    @FormUrlEncoded
    @POST("yemekler/sepeteYemekEkle.php")
    suspend fun sepeteYemekEkle(
        @Field("yemek_adi") yemekAdi: String,
        @Field("yemek_fiyat") yemekFiyati: Int,
        @Field("yemek_resim_adi") yemekResimAdi: String,
        @Field("yemek_siparis_adet") yemekSiparisAdet: Int,
        @Field("kullanici_adi") kullaniciAdi: String
    ): Response<YemekCevap>
}
