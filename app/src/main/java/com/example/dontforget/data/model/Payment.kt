package com.example.dontforget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subscriptionId: Long,
    val date: Long,
    val amount: Double,
    val status: PaymentStatus = PaymentStatus.PAID
) {
    enum class PaymentStatus { PAID, UNPAID }
}
