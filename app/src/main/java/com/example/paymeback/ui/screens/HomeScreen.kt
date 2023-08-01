package com.example.paymeback.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
            sortedBy = viewModel.sortedBy.collectAsState(initial = 0).value,
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
    sortedBy: Int,
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
        var expanded by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = MaterialTheme.spacing.extraLarge)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = stringResource(R.string.sort_text)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.last_modified_text)) },
                            onClick = {
                                onSortClick(SortOption.LAST_MODIFIED_DESCENDING.value)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ArrowDownward,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.last_modified_text)) },
                            onClick = {
                                onSortClick(SortOption.LAST_MODIFIED_ASCENDING.value)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ArrowUpward,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.balance_text)) },
                            onClick = {
                                onSortClick(SortOption.BALANCE_DESCENDING.value)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ArrowDownward,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.balance_text)) },
                            onClick = {
                                onSortClick(SortOption.BALANCE_ASCENDING.value)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ArrowUpward,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.person_name_text)) },
                            onClick = {
                                onSortClick(SortOption.NAME_DESCENDING.value)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ArrowDownward,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.person_name_text)) },
                            onClick = {
                                onSortClick(SortOption.NAME_ASCENDING.value)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ArrowUpward,
                                    contentDescription = null
                                )
                            })
                    }
                }
            }
            items(when (sortedBy) {
                SortOption.LAST_MODIFIED_DESCENDING.value -> recordList.sortedByDescending { it.modifiedAt }
                SortOption.LAST_MODIFIED_ASCENDING.value -> recordList.sortedBy { it.modifiedAt }
                SortOption.BALANCE_DESCENDING.value -> recordList.sortedByDescending { it.balance }
                SortOption.BALANCE_ASCENDING.value -> recordList.sortedBy { it.balance }
                SortOption.NAME_DESCENDING.value -> recordList.sortedByDescending { it.person.lowercase() }
                SortOption.BALANCE_ASCENDING.value -> recordList.sortedBy { it.person.lowercase() }
                else -> recordList
            }) { record ->
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
            supportingContent = {
                Text(
                    text = stringResource(R.string.last_modified_text).plus(": ").plus(
                        dateFormatter.format(record.modifiedAt)),
                    style = MaterialTheme.typography.labelMedium
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