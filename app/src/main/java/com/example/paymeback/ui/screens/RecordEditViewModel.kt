package com.example.paymeback.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymeback.data.OfflineRecordsRepository
import com.example.paymeback.data.Payment
import com.example.paymeback.data.Record
import com.example.paymeback.data.RecordWithPayments
import com.example.paymeback.ui.navigation.DEFAULT_ENTRY_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecordEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordsRepository: OfflineRecordsRepository
) : ViewModel() {
    private val recordId: Long = checkNotNull(savedStateHandle[RecordEditDestination.recordIdArg])

    private val _recordUiState: MutableStateFlow<RecordUiState> = MutableStateFlow(RecordUiState())
    val recordUiState: StateFlow<RecordUiState> = _recordUiState.asStateFlow()

    init {
        viewModelScope.launch {
            recordsRepository.getRecordWithPaymentsStream(recordId)
                .map {
                    if (recordId != DEFAULT_ENTRY_ID) {
                        it?.toRecordUiState() ?: RecordUiState(
                            actionEnabled = true,
                            isFirstTimeEntry = true
                        )
                    } else {
                        RecordUiState(
                            actionEnabled = true,
                            isFirstTimeEntry = true
                        )
                    }
                }
                .collect { newRecordUiState ->
                    _recordUiState.value = newRecordUiState
                }
        }
    }

    fun updateUiState(newRecordUiState: RecordUiState) {
        _recordUiState.update { newRecordUiState.copy() }
    }

    suspend fun updateRecord() {
        if (recordUiState.value.isValid()) {
            recordsRepository.updateRecord(recordUiState.value.toRecordWithPayments().record)
        }
        updateUiState(
            recordUiState.value.copy(
                actionEnabled = false,
                isFirstTimeEntry = false
            )
        )
    }

    suspend fun saveRecord() {
        if (recordUiState.value.isValid()) {
            val newRecordId = viewModelScope.async {
                recordsRepository.insertRecord(recordUiState.value.toRecordWithPayments().record)
            }
            viewModelScope.launch {
                recordsRepository.getRecordWithPaymentsStream(newRecordId.await())
                    .map {
                        it?.toRecordUiState() ?: RecordUiState()
                    }
                    .collect { newRecordUiState ->
                        _recordUiState.value = newRecordUiState
                    }
            }

        }
    }

    suspend fun deleteRecord() {
        recordsRepository.deleteRecord(recordUiState.value.toRecordWithPayments().record)
    }
}

data class RecordUiState(
    val id: Long = 0,
    val actionEnabled: Boolean = false,
    val isFirstTimeEntry: Boolean = false,
    val deleteDialogVisible: Boolean = false,
    val person: String = "",
    val balance: Float = 0F,
    val modifiedAt: Date = Date(),
    val payments: List<Payment> = listOf(),
)

fun RecordUiState.isValid(): Boolean {
    return person.isNotEmpty()
}

fun RecordUiState.getProportions(): List<Float> {
    val myPaymentsSum = this.payments.filter { it.isMyPayment }.map { it.amount }.sum()
    val personsPaymentsSum = this.payments.filter { !it.isMyPayment }.map { it.amount }.sum()
    val combinedSum = myPaymentsSum + personsPaymentsSum
    return listOf(myPaymentsSum / combinedSum, personsPaymentsSum / combinedSum)
}

fun RecordWithPayments.toRecordUiState(
    actionEnabled: Boolean = false,
    isFirstTimeEntry: Boolean = false,
    deleteDialogVisible: Boolean = false
): RecordUiState =
    RecordUiState(
        id = record.id,
        actionEnabled = actionEnabled,
        isFirstTimeEntry = isFirstTimeEntry,
        deleteDialogVisible = deleteDialogVisible,
        person = record.person,
        balance = record.balance,
        modifiedAt = record.modifiedAt,
        payments = payments
    )

fun RecordUiState.toRecordWithPayments(): RecordWithPayments = RecordWithPayments(
    record = Record(id, person, balance, Date(System.currentTimeMillis())),
    payments = payments
)