package com.example.paymeback.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

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
    val id: Long = 0,
    @ColumnInfo(name = "record_id")
    val recordId: Long,
    @ColumnInfo(name = "is_my_payment")
    val isMyPayment: Boolean,
    val date: Date,
    val title: String,
    val amount: Float
)
