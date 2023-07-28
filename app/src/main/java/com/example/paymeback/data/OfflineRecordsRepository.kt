package com.example.paymeback.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineRecordsRepository @Inject constructor(
    private val recordDao: RecordDao
) : RecordsRepository {
    override fun getAllRecordsStream(): Flow<List<Record>> = recordDao.getAllRecords()

    override fun getRecordStream(id: Int): Flow<Record?> = recordDao.getRecord(id)

    override fun getRecordWithPaymentsStream(id: Int): Flow<RecordWithPayments?> =
        recordDao.getRecordWithPayments(id)

    override suspend fun insertRecord(record: Record): Long = recordDao.insert(record)

    override suspend fun deleteRecord(record: Record) = recordDao.delete(record)

    override suspend fun updateRecord(record: Record) = recordDao.update(record)
}