package com.example.paymeback.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecordWithPayments(
    @Embedded val record: Record,
    @Relation(
        parentColumn = "id",
        entityColumn = "record_id"
    )
    val payments: List<Payment>
)