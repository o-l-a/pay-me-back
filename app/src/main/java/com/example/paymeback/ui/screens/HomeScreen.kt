package com.example.paymeback.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.example.paymeback.PayMeBackTopAppBar
import com.example.paymeback.R
import com.example.paymeback.data.Record
import com.example.paymeback.ui.navigation.DEFAULT_ENTRY_ID
import com.example.paymeback.ui.navigation.HOME_ROUTE
import com.example.paymeback.ui.navigation.NavigationDestination
import com.example.paymeback.ui.theme.spacing

object HomeDestination : NavigationDestination {
    override val route: String = HOME_ROUTE
    override val titleRes: Int = R.string.home_screen_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToRecordEdit: (Int) -> Unit,
    viewModel: HomeViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PayMeBackTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigateToRecordEdit(DEFAULT_ENTRY_ID) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_new_record))
            }
        }
    ) { innerPadding ->
        HomeBody(
            modifier = modifier.padding(innerPadding),
            recordList = homeUiState.recordList,
            onCardClick = navigateToRecordEdit
        )
    }
}

@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    recordList: List<Record>,
    onCardClick: (Int) -> Unit
) {
    if (recordList.isEmpty()) {
        Text(
            modifier = modifier.padding(
                MaterialTheme.spacing.medium
            ),
            text = stringResource(R.string.no_records_yet)
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(recordList) { record ->
                RecordCard(
                    record = record,
                    onCardClick = onCardClick
                )
            }
        }
    }
}

@Composable
fun RecordCard(
    modifier: Modifier = Modifier,
    record: Record,
    onCardClick: (Int) -> Unit
) {
    Card() {
        Text("hej")
    }
}