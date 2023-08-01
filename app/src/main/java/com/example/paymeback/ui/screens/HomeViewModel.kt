package com.example.paymeback.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymeback.data.OfflineRecordsRepository
import com.example.paymeback.data.Record
import com.example.paymeback.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption(val value: Int) {
    LAST_MODIFIED_DESCENDING(0),
    LAST_MODIFIED_ASCENDING(1),
    BALANCE_DESCENDING(2),
    BALANCE_ASCENDING(3),
    NAME_DESCENDING(4),
    NAME_ASCENDING(5)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    recordsRepository: OfflineRecordsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val sortedBy = userPreferencesRepository.recordsSortedBy

    val homeUiState: StateFlow<HomeUiState> =
        recordsRepository.getAllRecordsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    suspend fun changeSorting(sortBy: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveRecordSortingPreference(sortBy)
            Log.d("PREFERENCE", sortBy.toString())
        }
    }
}

data class HomeUiState(val recordList: List<Record> = listOf())