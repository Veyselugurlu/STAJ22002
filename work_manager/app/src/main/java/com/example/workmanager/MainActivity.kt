package com.example.workmanager

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanager.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
         /*  //internet varsa bildirim alicak yoksa almşcak
            val calismaKosulu = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

                val istek =  OneTimeWorkRequestBuilder<MyWorker>()
                .setInitialDelay(3,TimeUnit.SECONDS)
                .setConstraints(calismaKosulu)
                .build()

            //butona bastigimiz zman istegimizi calistiracak.
            WorkManager.getInstance(this).enqueue(istek)


        WorkManager.getInstance(this).getWorkInfoByIdLiveData(istek.id).observe(this){
            val durum = it.state.name
            Log.e("Arka Plan Durumu",durum)
        }*/
            val istek = PeriodicWorkRequestBuilder<MyWorkerBildirimi>(1,TimeUnit.MINUTES)
                .setInitialDelay(5,TimeUnit.SECONDS).build()
            WorkManager.getInstance(this).enqueue(istek)
        }
    }
}