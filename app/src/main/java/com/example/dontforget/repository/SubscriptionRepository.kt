package com.example.dontforget.data.repository

import com.example.dontforget.data.dao.PaymentDao
import com.example.dontforget.data.dao.SubscriptionDao
import com.example.dontforget.data.model.Payment
import com.example.dontforget.data.model.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDao,
    private val paymentDao: PaymentDao
) {

    // --- Subscriptions ---
    fun getAllSubscriptions(): Flow<List<Subscription>> = subscriptionDao.getAllFlow()

    suspend fun getSubscription(id: Long): Subscription? = withContext(Dispatchers.IO) {
        subscriptionDao.getById(id)
    }

    suspend fun insertSubscription(subscription: Subscription) = withContext(Dispatchers.IO) {
        subscriptionDao.insert(subscription)
    }

    suspend fun updateSubscription(subscription: Subscription) = withContext(Dispatchers.IO) {
        subscriptionDao.update(subscription)
    }

    suspend fun deleteSubscription(subscription: Subscription) = withContext(Dispatchers.IO) {
        subscriptionDao.delete(subscription)
    }

    // --- Payments ---
    fun getPaymentsForSubscription(subId: Long): Flow<List<Payment>> =
        paymentDao.paymentsFor(subId)

    suspend fun insertPayment(payment: Payment) = withContext(Dispatchers.IO) {
        paymentDao.insert(payment)
    }

    suspend fun updatePayment(payment: Payment) = withContext(Dispatchers.IO) {
        paymentDao.update(payment)
    }
}
