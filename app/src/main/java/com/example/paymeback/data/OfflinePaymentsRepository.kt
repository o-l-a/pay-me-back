package com.example.paymeback.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflinePaymentsRepository @Inject constructor(
    private val paymentDao: PaymentDao
) : PaymentsRepository {
    override fun getAllPaymentsStream(): Flow<List<Payment>> = paymentDao.getAllPayments()

    override fun getPaymentStream(id: Long): Flow<Payment?> = paymentDao.getPayment(id)

    override suspend fun insertPayment(payment: Payment): Long = paymentDao.insert(payment)

    override suspend fun deletePayment(payment: Payment) = paymentDao.delete(payment)

    override suspend fun updatePayment(payment: Payment) = paymentDao.update(payment)
}