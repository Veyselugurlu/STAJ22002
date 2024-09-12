package com.example.yemeksiparis.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yemeksiparis.data.YRepo
import com.example.yemeksiparis.model.Yemekler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnasayfaViewModel @Inject constructor(var yrepo: YRepo) : ViewModel() {

    val yemeklerListesi = MutableLiveData<List<Yemekler>>()

    init {
        yemekleriYukle()
    }

    fun yemekleriYukle() {
        viewModelScope.launch {
            try {
                val response = yrepo.yemekleriYukle()
                yemeklerListesi.value = response
            } catch (e: Exception) {
                Log.e("AnasayfaViewModel", "API isteği sırasında hata: ${e.message}")
            }
        }
    }
}
