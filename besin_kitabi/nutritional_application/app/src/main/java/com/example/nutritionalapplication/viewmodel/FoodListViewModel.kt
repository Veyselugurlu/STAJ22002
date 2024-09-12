package com.example.nutritionalapplication.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritionalapplication.model.Food
import com.example.nutritionalapplication.roomDb.FoodDatabase
import com.example.nutritionalapplication.service.FoodApiService
import com.example.nutritionalapplication.util.SpecialSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//contex'e ulaşabilmek icin android view model kullandik
class FoodListViewModel(application: Application) : AndroidViewModel(application){
    val foods= MutableLiveData<List<Food>>()
    val foodMessageError = MutableLiveData<Boolean>()
    val loadingFood = MutableLiveData<Boolean>()

    private val foodApiService = FoodApiService()
    private val specialSharedPreferences = SpecialSharedPreferences(getApplication())

    private val updateTime = 10 * 60 * 1000 * 1000 * 1000L

    fun refreshData(){
        val registerTime = specialSharedPreferences.getTime()

        if (registerTime != null && registerTime != 0L && System.nanoTime() - registerTime < updateTime){
            //roomdan verileri alma
            getDataFromRoom()
        }
        else{
                getDataFromInternet()
        }
    }
    fun refreshDataFromIntnet(){
        getDataFromInternet()
    }
    private fun getDataFromRoom(){
        loadingFood.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val foodList = FoodDatabase(getApplication()).foodDao().getAllFood()
            withContext(Dispatchers.Main){
                showfoods(foodList)
                Toast.makeText(getApplication(),"Besinleri Internetten Aldık",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getDataFromInternet(){
        loadingFood.value = true

        viewModelScope.launch(Dispatchers.IO){
           val foodList = foodApiService.getData()
            withContext(Dispatchers.Main){
                loadingFood.value = false
                roomRegister(foodList)
                Toast.makeText(getApplication(),"Besinelri Internetten Aldık",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showfoods(foodList: List<Food>){
        foods.value = foodList
        foodMessageError.value = false
        loadingFood.value = false
    }

    private fun roomRegister(foodList : List<Food>){
        viewModelScope.launch {
            val dao = FoodDatabase(getApplication()).foodDao()
            dao.deleteAllFood()
            val uuidListesi =dao.insertAll(*foodList.toTypedArray())
            var i = 0
            while (i < foodList.size){
                foodList[i].uuid = uuidListesi[i].toInt()
                i = i+1
            }
            showfoods(foodList)
        }
        // mevcut zamanin nanosecond cinsinden kaydedilmesini saglar
        specialSharedPreferences.timeRegister(System.nanoTime())
    }
}