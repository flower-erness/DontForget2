package com.example.dontforget.data.dao

import androidx.room.*
import com.example.dontforget.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE subscriptionId = :subId ORDER BY date DESC")
    fun paymentsFor(subId: Long): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment): Long

    @Update suspend fun update(payment: Payment)
}
