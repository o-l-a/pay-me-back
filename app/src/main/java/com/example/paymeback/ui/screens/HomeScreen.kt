package com.example.paymeback.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

object HomeDestination : NavigationDestination {
    override val route: String = HOME_ROUTE
    override val titleRes: Int = R.string.home_screen_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToRecordEdit: (Long) -> Unit,
    viewModel: HomeViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
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
            onCardClick = navigateToRecordEdit,
            onSortClick = { sortBy ->
                coroutineScope.launch {
                    viewModel.changeSorting(sortBy)
                }
            }
        )
    }
}

@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    recordList: List<Record>,
    onCardClick: (Long) -> Unit,
    onSortClick: (Int) -> Unit
) {
    if (recordList.isEmpty()) {
        Text(
            modifier = modifier.padding(
                MaterialTheme.spacing.medium
            ),
            text = stringResource(R.string.no_records_yet)
        )
    } else {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val symbol = numberFormat.currency?.symbol
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Row {
                    IconButton(onClick = { onSortClick(0) }) {
                        Icon(Icons.Filled.Headphones, null)
                    }
                    IconButton(onClick = { onSortClick(1) }) {
                        Icon(Icons.Filled.Headphones, null)
                    }
                }
            }
            items(recordList) { record ->
                RecordCard(
                    record = record,
                    currencySymbol = symbol,
                    onCardClick = onCardClick
                )
            }
        }
    }
}

@Composable
fun RecordCard(
    modifier: Modifier = Modifier,
    currencySymbol: String?,
    record: Record,
    onCardClick: (Long) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .padding(
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.medium,
                top = MaterialTheme.spacing.small,
                bottom = MaterialTheme.spacing.small
            )
            .fillMaxWidth()
            .clickable(
                onClick = { onCardClick(record.id) }
            )
    ) {
        ListItem(
            modifier = Modifier
                .padding(
                    top = MaterialTheme.spacing.small,
                    bottom = MaterialTheme.spacing.small
                ),
            trailingContent = {
                Text(
                    text = decimalFormat.format(record.balance).plus(" ").plus(currencySymbol),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            headlineContent = {
                Text(
                    text = record.person
                )
            },
            leadingContent = {
                Icon(
                    imageVector = when {
                        record.balance > 0 -> Icons.Filled.TrendingUp
                        record.balance < 0 -> Icons.Filled.TrendingDown
                        else -> Icons.Filled.Done
                    },
                    contentDescription = null
                )
            }
        )
    }
}