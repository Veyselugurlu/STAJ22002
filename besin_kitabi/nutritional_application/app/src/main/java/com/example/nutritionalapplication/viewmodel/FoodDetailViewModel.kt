package com.example.nutritionalapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutritionalapplication.model.Food
import com.example.nutritionalapplication.roomDb.FoodDatabase
import kotlinx.coroutines.launch
import java.util.UUID

class FoodDetailViewModel(application: Application) : AndroidViewModel(application){

    val foodLiveData = MutableLiveData<Food>()

    fun getDataroom(uuid: Int){
        viewModelScope.launch {
            val dao = FoodDatabase(getApplication()).foodDao()
            val food = dao.getFood(uuid)
            foodLiveData.value = food
        }
    }
}