package com.example.paymeback.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
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
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE Record ADD COLUMN modified_at INTEGER NOT NULL DEFAULT 0
                """.trimIndent())
            }
        }

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE Payment ADD COLUMN payment_group TEXT NOT NULL DEFAULT ''
                """.trimIndent())
            }
        }

        private val DB_CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    """
                    CREATE TRIGGER update_record_balance_after_insert
                    AFTER INSERT ON Payment
                    BEGIN
                        UPDATE Record
                        SET balance = (
                            SELECT COALESCE(SUM(CASE WHEN is_my_payment = 1 THEN amount ELSE -amount END), 0)
                            FROM Payment
                            WHERE record_id = NEW.record_id
                        )
                        WHERE id = NEW.record_id;
                    END;
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TRIGGER update_record_balance_after_update
                    AFTER UPDATE ON Payment
                    BEGIN
                        UPDATE Record
                        SET balance = (
                            SELECT COALESCE(SUM(CASE WHEN is_my_payment = 1 THEN amount ELSE -amount END), 0)
                            FROM Payment
                            WHERE record_id = NEW.record_id
                        )
                        WHERE id = NEW.record_id;
                    END;
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TRIGGER update_record_balance_after_delete
                    AFTER DELETE ON Payment
                    BEGIN
                        UPDATE Record
                        SET balance = (
                            SELECT COALESCE(SUM(CASE WHEN is_my_payment = 1 THEN amount ELSE -amount END), 0)
                            FROM Payment
                            WHERE record_id = OLD.record_id
                        )
                        WHERE id = OLD.record_id;
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
        ).addMigrations(MIGRATION_9_10)
            .addMigrations(MIGRATION_11_12)
            .fallbackToDestructiveMigration()
            .addCallback(DB_CALLBACK)
            .build()
    }
}