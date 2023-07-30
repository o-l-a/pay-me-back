package com.example.paymeback.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * from record ORDER BY modified_at DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * from record WHERE id = :id")
    fun getRecord(id: Long): Flow<Record>

    @Query("SELECT * from record WHERE id = :id")
    fun getRecordWithPayments(id: Long): Flow<RecordWithPayments>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Record): Long

    @Update
    suspend fun update(item: Record)

    @Delete
    suspend fun delete(item: Record)
}