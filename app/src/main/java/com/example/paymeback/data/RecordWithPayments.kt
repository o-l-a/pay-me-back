package com.example.paymeback.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecordWithPayments(
    @Embedded val record: Record,
    @Relation(
        parentColumn = "id",
        entityColumn = "record_id"
    )
    val myPayments: List<Payment>,
    @Relation(
        parentColumn = "id",
        entityColumn = "record_id"
    )
    val personsPayments: List<Payment>
)