package com.example.dontforget.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dontforget.DontForgetApplication
import com.example.dontforget.data.repository.SubscriptionRepository
import com.example.dontforget.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class NotificationWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val repo = SubscriptionRepository(
        DontForgetApplication.database.subscriptionDao(),
        DontForgetApplication.database.paymentDao()
    )

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val next24h = currentTime + TimeUnit.HOURS.toMillis(24)

        val subs = repo.getAllSubscriptions()
        var reminderCount = 0

        subs.collect { list ->
            list.filter { it.reminderEnabled && it.nextDue in currentTime..next24h }
                .forEach {
                    NotificationHelper.showReminder(
                        applicationContext,
                        "Upcoming Renewal: ${it.name}",
                        "Your ${it.name} subscription renews soon for $${it.cost}"
                    )
                    reminderCount++
                }
        }

        if (reminderCount > 0) Result.success() else Result.retry()
    }
}
