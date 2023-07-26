package com.example.paymeback.ui.screens

import androidx.lifecycle.ViewModel
import com.example.paymeback.data.OfflinePaymentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentEditViewModel @Inject constructor(
    private val paymentsRepository: OfflinePaymentsRepository
) : ViewModel() {
}