package com.example.dontforget.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dontforget.data.dao.PaymentDao
import com.example.dontforget.data.dao.SubscriptionDao
import com.example.dontforget.data.model.Payment
import com.example.dontforget.data.model.Subscription

@Database(
    entities = [Subscription::class, Payment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentDao(): PaymentDao
}
