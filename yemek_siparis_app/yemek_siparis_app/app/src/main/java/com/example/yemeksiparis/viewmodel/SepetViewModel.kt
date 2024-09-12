package com.example.yemeksiparis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yemeksiparis.data.YRepo
import com.example.yemeksiparis.model.Yemekler
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SepetViewModel @Inject constructor(private val yrepo: YRepo) : ViewModel() {
    private val _sepetYemekListesi = MutableLiveData<List<Yemekler>>()
    val sepetYemekListesi: LiveData<List<Yemekler>> get() = _sepetYemekListesi

    init {
        loadSepetYemekler()
    }

    fun loadSepetYemekler() {
        viewModelScope.launch {
            try {
                // Sepetteki yemekleri yükleyin
                val sepetYemekler = yrepo.yemekleriYukle() // Belki burayı değiştirmelisiniz
                _sepetYemekListesi.value = sepetYemekler
            } catch (e: Exception) {
                Log.e("SepetViewModel", "API isteği sırasında hata: ${e.message}")
            }
        }
    }

    fun addYemekToSepet(yemek: Yemekler) {
        val updatedList = _sepetYemekListesi.value.orEmpty().toMutableList()
        val existingYemek = updatedList.find { it.yemek_id == yemek.yemek_id }
        if (existingYemek != null) {
            // Mevcut yemek varsa, adetini güncelle
            existingYemek.yemek_siparis_adet = yemek.yemek_siparis_adet
        } else {
            // Yeni yemek ekle
            updatedList.add(yemek)
        }
        _sepetYemekListesi.value = updatedList
    }
}
