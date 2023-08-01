package com.example.paymeback.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Record::class, Payment::class],
    version = 11,
    exportSchema = false
)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun paymentDao(): PaymentDao
}