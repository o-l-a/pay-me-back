package com.example.paymeback.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RecordDatabaseModule {
    @Provides
    fun provideRecordDao(recordDatabase: RecordDatabase): RecordDao {
        return recordDatabase.recordDao()
    }

    @Provides
    fun providePaymentDao(recordDatabase: RecordDatabase): PaymentDao {
        return recordDatabase.paymentDao()
    }

    companion object {
        private val DB_CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    """
                    CREATE TRIGGER update_record_balance AFTER INSERT ON Payment
                    BEGIN
                        UPDATE Record
                        SET balance = (
                            SELECT SUM(CASE WHEN isMyPayment = 1 THEN amount ELSE -amount END)
                            FROM Payment
                            WHERE record_id = NEW.record_id
                        )
                        WHERE id = NEW.record_id;
                    END;
                    """.trimIndent()
                )
            }
        }
    }

    @Provides
    @Singleton
    fun provideRecordDatabase(@ApplicationContext appContext: Context): RecordDatabase {
        return Room.databaseBuilder(
            appContext,
            RecordDatabase::class.java,
            "PayMeBackDatabase"
        ).fallbackToDestructiveMigration()
            .addCallback(DB_CALLBACK)
            .build()
    }
}