package com.example.paymeback.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val person: String,
    val balance: Float
)

