package com.example.paymeback.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * from payment ORDER BY date DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * from payment WHERE id = :id")
    fun getPayment(id: Int): Flow<Payment>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Payment)

    @Update
    suspend fun update(item: Payment)

    @Delete
    suspend fun delete(item: Payment)
}