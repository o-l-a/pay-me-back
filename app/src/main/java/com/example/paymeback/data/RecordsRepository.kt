package com.example.paymeback.data

import kotlinx.coroutines.flow.Flow

interface RecordsRepository {
    fun getAllRecordsStream(): Flow<List<Record>>

    fun getRecordStream(id: Int): Flow<Record?>

    fun getRecordWithPaymentsStream(id: Int): Flow<RecordWithPayments?>

    suspend fun insertRecord(record: Record)

    suspend fun deleteRecord(record: Record)

    suspend fun updateRecord(record: Record)
}