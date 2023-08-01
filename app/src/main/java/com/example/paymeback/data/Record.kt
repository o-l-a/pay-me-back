package com.example.paymeback.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity
@TypeConverters(Converters::class)
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val person: String,
    val balance: Float,
    @ColumnInfo(name = "modified_at")
    val modifiedAt: Date // added in version 10
)
