package com.example.dontforget

import android.app.Application
import androidx.room.Room
import androidx.work.*
import com.example.dontforget.data.db.AppDatabase
import com.example.dontforget.notification.NotificationWorker
import java.util.concurrent.TimeUnit

class DontForgetApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "dont_forget_db"
        )
            .fallbackToDestructiveMigration()
            .build()
        scheduleNotificationWorker()
    }
    private fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS) // optional
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SubscriptionReminderWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
