package com.example.paymeback.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymeback.data.OfflinePaymentsRepository
import com.example.paymeback.data.OfflineRecordsRepository
import com.example.paymeback.data.Payment
import com.example.paymeback.ui.navigation.DEFAULT_ENTRY_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PaymentEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paymentsRepository: OfflinePaymentsRepository,
    private val recordsRepository: OfflineRecordsRepository
) : ViewModel() {
    var paymentUiState by mutableStateOf(PaymentUiState())

    private val paymentId: Long =
        checkNotNull(savedStateHandle[PaymentEditDestination.paymentIdArg])
    private val recordId: Long = checkNotNull(savedStateHandle[PaymentEditDestination.recordIdArg])

    init {
        viewModelScope.launch {
            paymentUiState = if (paymentId != DEFAULT_ENTRY_ID) {
                paymentsRepository.getPaymentStream(paymentId)
                    .filterNotNull()
                    .first()
                    .toPaymentUiState()
            } else {
                PaymentUiState(
                    isFirstTimeEntry = true,
                    recordId = recordId
                )
            }
        }
    }

    fun updateUiState(newRecordUiState: PaymentUiState) {
        paymentUiState = newRecordUiState.copy(
            isFormValid = paymentUiState.isValid()
        )
    }

    suspend fun updatePayment() {
        if (paymentUiState.isValid()) {
            paymentsRepository.updatePayment(paymentUiState.toPayment())
        }
        updateUiState(
            paymentUiState.copy(
                isFirstTimeEntry = false
            )
        )
    }

    suspend fun savePayment() {
        if (paymentUiState.isValid()) {
            val newPaymentId = viewModelScope.async {
                paymentsRepository.insertPayment(paymentUiState.toPayment())
            }
            updateUiState(
                paymentUiState.copy(
                    id = newPaymentId.await(),
                    isFirstTimeEntry = false
                )
            )
        }
    }

    suspend fun deletePayment() {
        paymentsRepository.deletePayment(paymentUiState.toPayment())
    }

    suspend fun updateRecord() {
        val record = recordsRepository.getRecordStream(recordId).first()
        if (record != null) {
            recordsRepository.updateRecord(record.copy(modifiedAt = Date(System.currentTimeMillis())))
        }
    }
}

data class PaymentUiState(
    val id: Long = 0,
    val recordId: Long = 0,
    val isMyPayment: Boolean = true,
    val date: Date = Date(),
    val title: String = "",
    val amount: String = "",
    val person: String = "",
    val deleteDialogVisible: Boolean = false,
    val datePickerDialogVisible: Boolean = false,
    val isFormValid: Boolean = false,
    val isFirstTimeEntry: Boolean = false,
)

fun Payment.toPaymentUiState(
    person: String = "",
    deleteDialogVisible: Boolean = false,
    datePickerDialogVisible: Boolean = false,
    isFormValid: Boolean = false,
    isFirstTimeEntry: Boolean = false
): PaymentUiState = PaymentUiState(
    id = id,
    recordId = recordId,
    isMyPayment = isMyPayment,
    date = date,
    title = title,
    amount = amount.toString(),
    person = person,
    deleteDialogVisible = deleteDialogVisible,
    datePickerDialogVisible = datePickerDialogVisible,
    isFormValid = isFormValid,
    isFirstTimeEntry = isFirstTimeEntry
)

fun PaymentUiState.toPayment(): Payment = Payment(
    id = id,
    recordId = recordId,
    isMyPayment = isMyPayment,
    date = date,
    title = title,
    amount = amount.toFloatOrNull() ?: 0.0F
)

fun PaymentUiState.isValid(): Boolean {
    return title.isNotEmpty() && (amount.isNotBlank())
}