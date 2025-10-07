package com.example.dontforget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dontforget.DontForgetApplication
import com.example.dontforget.data.model.Payment
import com.example.dontforget.data.model.Subscription
import com.example.dontforget.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel() {

    private val repo = SubscriptionRepository(
        DontForgetApplication.database.subscriptionDao(),
        DontForgetApplication.database.paymentDao()
    )

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions

    init {
        // Observe DB changes reactively
        viewModelScope.launch {
            repo.getAllSubscriptions().collectLatest { list ->
                _subscriptions.value = list
            }
        }
    }

    fun addSubscription(sub: Subscription) {
        viewModelScope.launch { repo.insertSubscription(sub) }
    }

    fun updateSubscription(sub: Subscription) {
        viewModelScope.launch { repo.updateSubscription(sub) }
    }

    fun deleteSubscription(sub: Subscription) {
        viewModelScope.launch { repo.deleteSubscription(sub) }
    }

    fun recordPayment(payment: Payment) {
        viewModelScope.launch { repo.insertPayment(payment) }
    }

    fun updatePayment(payment: Payment) {
        viewModelScope.launch { repo.updatePayment(payment) }
    }
}
