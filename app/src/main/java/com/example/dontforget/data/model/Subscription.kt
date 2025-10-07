package com.example.dontforget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cost: Double,
    val cycle: Cycle = Cycle.MONTHLY,
    val nextDue: Long,
    val category: String? = null,
    val reminderEnabled: Boolean = true
) {
    enum class Cycle { MONTHLY, YEARLY, WEEKLY, CUSTOM }
}
