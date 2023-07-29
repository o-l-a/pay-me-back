package com.example.paymeback.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Record::class, Payment::class], version = 8, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun paymentDao(): PaymentDao

//    companion object {
        //        @Volatile
//        private var Instance: RecordDatabase? = null
//
//        fun getDatabase(context: Context): RecordDatabase {
//            return Instance ?: synchronized(this) {
//                Room.databaseBuilder(context, RecordDatabase::class.java, "record_database")
//                    .fallbackToDestructiveMigration()
//                    .build()
//                    .also { Instance = it }
//            }
//        }
}