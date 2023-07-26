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
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordsRepository: OfflineRecordsRepository
) : ViewModel() {

    var homeUiState by mutableStateOf(RecordUiState())

    private val recordId: Int? = savedStateHandle[HomeDestination.recordIdArg]

    init {
        viewModelScope.launch {
            homeUiState = if (recordId != null) {
                recordsRepository.getRecordWithPaymentsStream(recordId)
                    .filterNotNull()
                    .first()
                    .toRecordUiState(actionEnabled = false)
            } else {
                RecordUiState(actionEnabled = true)
            }
        }
    }
}

data class RecordUiState(
    val id: Int? = null,
    val actionEnabled: Boolean = false,
    val person: String = "",
    val myPayments: List<Payment> = listOf(),
    val personsPayments: List<Payment> = listOf(),
)

fun RecordWithPayments.toRecordUiState(actionEnabled: Boolean = false): RecordUiState = RecordUiState(
    id = record.id,
    actionEnabled = actionEnabled,
    person = record.person,
    myPayments = myPayments,
    personsPayments = personsPayments
)

fun RecordUiState.toRecordWithPayments(): RecordWithPayments = RecordWithPayments(
    record = Record(id!!, person),
    myPayments = myPayments,
    personsPayments = personsPayments
)