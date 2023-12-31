package com.example.paymeback.data

import kotlinx.coroutines.flow.Flow

interface RecordsRepository {
    fun getAllRecordsStream(): Flow<List<Record>>

    fun getRecordStream(id: Long): Flow<Record?>

    fun getRecordWithPaymentsStream(id: Long): Flow<RecordWithPayments?>

    suspend fun insertRecord(record: Record): Long

    suspend fun deleteRecord(record: Record)

    suspend fun updateRecord(record: Record)
}