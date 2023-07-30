package com.example.paymeback.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val person: String,
    val balance: Float,
    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long // added in version 10
)
