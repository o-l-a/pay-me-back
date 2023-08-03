package com.example.paymeback.data

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.paymeback.R
import java.util.Date

enum class PaymentCategory(@StringRes val label: Int) {
    ALCOHOL(R.string.alcohol_group),
    FOOD(R.string.food_group),
    CLOTHES(R.string.clothes_group),
    SPORT(R.string.sport_group),
    HOUSING(R.string.housing_group),
    FUN(R.string.fun_group),
    FUEL(R.string.fuel_group),
    LOAN(R.string.loan_group),
    TRAVEL(R.string.travel_group),
    EMERGENCY(R.string.emergency_group),
    SERVICES(R.string.services_group)
}

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
    val amount: Float,
    @ColumnInfo(name = "payment_group")
    val paymentGroup: String // added in ver. 12
)
