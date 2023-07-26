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
    @Query("SELECT * from record ORDER BY id DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * from record WHERE id = :id")
    fun getRecord(id: Int): Flow<Record>

    @Query("SELECT * from record WHERE id = :id")
    fun getRecordWithPayments(id: Int): Flow<RecordWithPayments>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Record)

    @Update
    suspend fun update(item: Record)

    @Delete
    suspend fun delete(item: Record)
}