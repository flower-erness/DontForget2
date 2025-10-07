package com.example.dontforget.data.dao

import androidx.room.*
import com.example.dontforget.data.model.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextDue ASC")
    fun getAllFlow(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): Subscription?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: Subscription): Long

    @Update suspend fun update(subscription: Subscription)
    @Delete suspend fun delete(subscription: Subscription)
}
