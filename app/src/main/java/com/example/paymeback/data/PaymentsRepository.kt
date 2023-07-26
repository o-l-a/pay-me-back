package com.example.paymeback.data

import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    fun getAllPaymentsStream(): Flow<List<Payment>>

    fun getPaymentStream(id: Int): Flow<Payment?>

    suspend fun insertPayment(payment: Payment)

    suspend fun deletePayment(payment: Payment)

    suspend fun updatePayment(payment: Payment)
}