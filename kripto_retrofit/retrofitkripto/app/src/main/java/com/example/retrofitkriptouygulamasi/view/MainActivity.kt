package com.example.retrofitkriptouygulamasi.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitkriptouygulamasi.adapter.RecyclerViewAdapter
import com.example.retrofitkriptouygulamasi.databinding.ActivityMainBinding
import com.example.retrofitkriptouygulamasi.model.CryptoModel
import com.example.retrofitkriptouygulamasi.service.CryptoApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.Listener {

    private val BASE_URL = "https://raw.githubusercontent.com/"
    private var cryptoModels: ArrayList<CryptoModel>? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private lateinit var binding: ActivityMainBinding

    //Dısposable
    private var compositeDisposable: CompositeDisposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compositeDisposable = CompositeDisposable()

        binding.recyclerView.layoutManager = LinearLayoutManager(this) // Doğru ID'yi kullanın

        loadData()
    }

    private fun loadData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CryptoApi::class.java)

            compositeDisposable?.add(retrofit.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))

/*
  val service = retrofit.create(CryptoApi::class.java)
  val call = service.getData()
        call.enqueue(object : Callback<List<CryptoModel>> {
            override fun onResponse(call: Call<List<CryptoModel>>, response: Response<List<CryptoModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        cryptoModels = ArrayList(it)
                        cryptoModels?.let {
                            recyclerViewAdapter = RecyclerViewAdapter(cryptoModels!!, this@MainActivity)
                            binding.recyclerView.adapter = recyclerViewAdapter // Doğru ID'yi kullanın
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
                t.printStackTrace()
            }
        })
        */
    }
    private fun handleResponse(cryptoList: List<CryptoModel>){
        cryptoModels = ArrayList(cryptoList)
        cryptoModels?.let {
            recyclerViewAdapter= RecyclerViewAdapter(it,this@MainActivity)
            binding.recyclerView.adapter = recyclerViewAdapter
        }

    }
    override fun onItemClick(cryptoModel: CryptoModel) {
        Toast.makeText(this, "Clicked ${cryptoModel.currency}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}
