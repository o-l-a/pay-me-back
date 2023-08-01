package com.example.paymeback.ui.screens

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymeback.R
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

enum class SortOption(
    val value: Int,
    @StringRes val label: Int,
    val icon: ImageVector
) {
    LAST_MODIFIED_DESCENDING(0, R.string.last_modified_text, Icons.Filled.ArrowDownward),
    LAST_MODIFIED_ASCENDING(1, R.string.last_modified_text, Icons.Filled.ArrowUpward),
    BALANCE_DESCENDING(2, R.string.balance_text, Icons.Filled.ArrowDownward),
    BALANCE_ASCENDING(3, R.string.balance_text, Icons.Filled.ArrowUpward),
    NAME_DESCENDING(4, R.string.person_name_text, Icons.Filled.ArrowDownward),
    NAME_ASCENDING(5, R.string.person_name_text, Icons.Filled.ArrowUpward)
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