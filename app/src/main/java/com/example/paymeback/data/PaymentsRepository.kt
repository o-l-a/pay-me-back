package com.example.paymeback.data

import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    fun getAllPaymentsStream(): Flow<List<Payment>>

    fun getPaymentStream(id: Long): Flow<Payment?>

    suspend fun insertPayment(payment: Payment): Long

    suspend fun deletePayment(payment: Payment)

    suspend fun updatePayment(payment: Payment)
}