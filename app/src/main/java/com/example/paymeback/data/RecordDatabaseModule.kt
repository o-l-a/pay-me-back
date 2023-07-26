package com.example.paymeback.data

import android.content.Context
import androidx.room.Room
import dagger.Binds
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

    @Provides
    @Singleton
    fun provideRecordDatabase(@ApplicationContext appContext: Context): RecordDatabase {
        return Room.databaseBuilder(
            appContext,
            RecordDatabase::class.java,
            "PayMeBackDatabase"
        ).build()
    }
}