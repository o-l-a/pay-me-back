package com.example.paymeback.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordsRepository: OfflineRecordsRepository
) : ViewModel() {
    var recordUiState by mutableStateOf(RecordUiState())

    private val recordId: Long = checkNotNull(savedStateHandle[RecordEditDestination.recordIdArg])

    init {
        viewModelScope.launch {
            recordUiState = if (recordId != DEFAULT_ENTRY_ID) {
                recordsRepository.getRecordWithPaymentsStream(recordId)
                    .filterNotNull()
                    .first()
                    .toRecordUiState()
            } else {
                RecordUiState(actionEnabled = true, isFirstTimeEntry = true)
            }
        }
    }

    fun updateUiState(newRecordUiState: RecordUiState) {
        recordUiState = newRecordUiState.copy()
    }

    suspend fun updateRecord() {
        if (recordUiState.isValid()) {
            recordsRepository.updateRecord(recordUiState.toRecordWithPayments().record)
        }
        updateUiState(
            recordUiState.copy(
                actionEnabled = false,
                isFirstTimeEntry = false
            )
        )
    }

    suspend fun saveRecord() {
        if (recordUiState.isValid()) {
            val newRecordId = viewModelScope.async {
                recordsRepository.insertRecord(recordUiState.toRecordWithPayments().record)
            }
            updateUiState(recordUiState.copy(
                id = newRecordId.await(),
                actionEnabled = false,
                isFirstTimeEntry = false
            ))
        }
    }

    suspend fun deleteRecord() {
        recordsRepository.deleteRecord(recordUiState.toRecordWithPayments().record)
    }
}

data class RecordUiState(
    val id: Long = 0,
    val actionEnabled: Boolean = false,
    val isFirstTimeEntry: Boolean = false,
    val deleteDialogVisible: Boolean = false,
    val person: String = "",
    val balance: Float = 0F,
    val payments: List<Payment> = listOf(),
)

fun RecordUiState.isValid(): Boolean {
    return person.isNotEmpty()
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
        payments = payments
    )

fun RecordUiState.toRecordWithPayments(): RecordWithPayments = RecordWithPayments(
    record = Record(id, person, balance),
    payments = payments
)