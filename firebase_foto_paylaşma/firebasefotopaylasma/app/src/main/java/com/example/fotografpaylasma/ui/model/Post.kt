package com.example.fotografpaylasma.ui.model


data class Post(
    val kullaniciAdi: String,
    val email: String?,
    val comment : String,
    val downloadUrl : String,
    var begeniSayisi: Int =0,
    var likedBy: List<String> = listOf() ,
    val documentId: String // Her postun benzersiz ID'si
)
