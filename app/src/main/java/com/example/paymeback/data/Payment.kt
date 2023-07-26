package com.example.paymeback.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = Record::class,
        parentColumns = ["id"],
        childColumns = ["record_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["record_id"])]
)
@TypeConverters(Converters::class)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "record_id")
    val recordId: Int,
    val date: LocalDateTime,
    val title: String,
    val amount: Float
)
