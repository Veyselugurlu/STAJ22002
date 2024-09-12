package com.example.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorkerBildirimi(appContext: Context, workerParameters: WorkerParameters): Worker(appContext,workerParameters) {

    override fun doWork(): Result {
        bildirimOlustur()
        return Result.success()
    }

    private fun bildirimOlustur() {
        val builder: NotificationCompat.Builder
        val bildirimYoneticisi = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext,MainActivity::class.java)
        val gidilecekIntent = PendingIntent.getActivity(applicationContext,1,intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            //kanal yapisinda aynı grupta olan bildirimeler gruplanir.
            val kanalId = "KanalId"
            val kanalAd = "KanalAd"
            val kanalAciklama = "KanalAçıklama"
            val kanalInceligi = NotificationManager.IMPORTANCE_HIGH

            var kanal : NotificationChannel? = bildirimYoneticisi.getNotificationChannel(kanalId)
            if (kanal == null){
                kanal = NotificationChannel(kanalId,kanalAd,kanalInceligi)
                kanal.description = kanalAciklama
                bildirimYoneticisi.createNotificationChannel(kanal)
            }

            builder = NotificationCompat.Builder(applicationContext,kanalId)

            builder.setContentTitle("Başlık")
                .setContentText("İçerik")
                .setSmallIcon(R.drawable.resim)
                .setContentIntent(gidilecekIntent)
                .setAutoCancel(true)
        }
    else
    {
        builder = NotificationCompat.Builder(applicationContext)
        builder.setContentTitle("Başlık")
            .setContentText("İçerik")
            .setSmallIcon(R.drawable.resim)
            .setContentIntent(gidilecekIntent)
            .setAutoCancel(true)
            .priority = Notification.PRIORITY_HIGH
    }
        bildirimYoneticisi.notify(1,builder.build())
}
}