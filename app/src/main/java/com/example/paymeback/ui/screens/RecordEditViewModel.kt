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
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val recordId: Int = checkNotNull(savedStateHandle[RecordEditDestination.recordIdArg])

    init {
        viewModelScope.launch {
            recordUiState = if (recordId != -1) {
                recordsRepository.getRecordWithPaymentsStream(recordId)
                    .filterNotNull()
                    .first()
                    .toRecordUiState(actionEnabled = false)
            } else {
                RecordUiState(actionEnabled = true, isFirstTimeEntry = true)
            }
        }
    }

    fun updateUiState(newRecordUiState: RecordUiState) {
        recordUiState = newRecordUiState.copy(
            balance = recordUiState.myPayments.map { it.amount }.sum() - recordUiState.personsPayments.map { it.amount }.sum()
        )
    }

    suspend fun updateRecord() {
        if (recordUiState.isValid()) {
            recordsRepository.updateRecord(recordUiState.toRecordWithPayments().record)
        }
    }

    suspend fun saveRecord() {
        if (recordUiState.isValid()) {
            recordsRepository.insertRecord(recordUiState.toRecordWithPayments().record)
        }
    }

    suspend fun deleteRecord() {
        recordsRepository.deleteRecord(recordUiState.toRecordWithPayments().record)
    }
}

data class RecordUiState(
    val id: Int = 0,
    val actionEnabled: Boolean = false,
    val isFirstTimeEntry: Boolean = false,
    val deleteDialogVisible: Boolean = false,
    val person: String = "",
    val balance: Float = 0f,
    val myPayments: List<Payment> = listOf(),
    val personsPayments: List<Payment> = listOf(),
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
        myPayments = myPayments,
        personsPayments = personsPayments
    )

fun RecordUiState.toRecordWithPayments(): RecordWithPayments = RecordWithPayments(
    record = Record(id, person, balance),
    myPayments = myPayments,
    personsPayments = personsPayments
)