package com.example.dontforget.worker

import android.content.Context
import androidx.work.*
import com.example.dontforget.notification.NotificationWorker
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "SubscriptionReminderWorker"

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
