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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                .map { it?.toRecordUiState() ?: RecordUiState(actionEnabled = true, isFirstTimeEntry = true) }
                .collect { newRecordUiState ->
                    _recordUiState.value = newRecordUiState
                }
        }
    }

//    val recordUiState: StateFlow<RecordUiState> =
//        recordsRepository.getRecordWithPaymentsStream(recordId).map { it?.toRecordUiState() ?: RecordUiState(actionEnabled = true, isFirstTimeEntry = true) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = RecordUiState(actionEnabled = true, isFirstTimeEntry = true)
//            )
//
////    init {
////        viewModelScope.launch {
////            recordUiState = if (recordId != DEFAULT_ENTRY_ID) {
////                recordsRepository.getRecordWithPaymentsStream(recordId)
////                    .filterNotNull()
////                    .first()
////                    .toRecordUiState()
////            } else {
////                RecordUiState()
////            }
////        }
////    }

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
            updateUiState(recordUiState.value.copy(
                id = newRecordId.await(),
                actionEnabled = false,
                isFirstTimeEntry = false
            ))
        }
    }

    suspend fun deleteRecord() {
        recordsRepository.deleteRecord(recordUiState.value.toRecordWithPayments().record)
    }
}

data class RecordUiState(
    val id: Long = DEFAULT_ENTRY_ID,
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