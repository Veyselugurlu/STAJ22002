package com.example.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, workerParameters: WorkerParameters): Worker(appContext,workerParameters) {
    override fun doWork(): Result {
        val toplam = 10 + 20
        Log.e("Arka plan İşlemi Sonucu", toplam.toString())
        return Result.success()

    }


}