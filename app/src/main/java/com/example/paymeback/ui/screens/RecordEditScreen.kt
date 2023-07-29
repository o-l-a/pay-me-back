package com.example.paymeback.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.example.paymeback.DeleteAlertDialog
import com.example.paymeback.PayMeBackTopAppBar
import com.example.paymeback.R
import com.example.paymeback.data.Payment
import com.example.paymeback.ui.navigation.DEFAULT_ENTRY_ID
import com.example.paymeback.ui.navigation.NavigationDestination
import com.example.paymeback.ui.navigation.RECORD_EDIT_ROUTE
import com.example.paymeback.ui.theme.spacing
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

object RecordEditDestination : NavigationDestination {
    override val route: String = RECORD_EDIT_ROUTE
    override val titleRes: Int = R.string.record_screen_title
    const val recordIdArg = "recordId"
    val routeWithArgs = "$route/{$recordIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToPaymentEdit: (Long, Long) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: RecordEditViewModel
) {
    val recordUiState = viewModel.recordUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val symbol = numberFormat.currency?.symbol
    Scaffold(
        topBar = {
            PayMeBackTopAppBar(
                title = recordUiState.value.person,
                navigateUp = onNavigateUp,
                canNavigateBack = true,
                hasDeleteAction = true,
                onDelete = { viewModel.updateUiState(recordUiState.value.copy(deleteDialogVisible = true)) },
                hasEditAction = true,
                onEdit = { viewModel.updateUiState(recordUiState.value.copy(actionEnabled = true)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigateToPaymentEdit(
                    recordUiState.value.id,
                    DEFAULT_ENTRY_ID
                )
            }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_payment)
                )
            }
        }
    ) { innerPadding ->
        Text(
            modifier = modifier.padding(
                MaterialTheme.spacing.medium
            ),
            text = "Mamy " + recordUiState.value.payments.size.toString()
        )
        if (recordUiState.value.payments.isEmpty()) {
            Text(
                modifier = modifier.padding(
                    MaterialTheme.spacing.medium
                ),
                text = stringResource(R.string.no_payments_yet)
            )
        } else {
            LazyColumn(
                modifier = modifier.padding(innerPadding)
            ) {
                items(recordUiState.value.payments) { payment ->
                    PaymentCard(
                        payment = payment,
                        onCardClick = navigateToPaymentEdit,
                        currencySymbol = symbol
                    )
                }
            }
        }
        if (recordUiState.value.actionEnabled) {
            EditRecordPopUp(
                recordUiState = recordUiState.value,
                onValueChange = viewModel::updateUiState,
                onDismiss = {
                    viewModel.updateUiState(recordUiState.value.copy(actionEnabled = false))
                    if (recordUiState.value.isFirstTimeEntry) {
                        navigateBack()
                    }
                },
                onConfirm = {
                    coroutineScope.launch {
                        if (recordUiState.value.isFirstTimeEntry) {
                            viewModel.saveRecord()
                        } else {
                            viewModel.updateRecord()
                        }
                    }
                }
            )
        }
        if (recordUiState.value.deleteDialogVisible) {
            DeleteAlertDialog(
                onDismiss = {
                    viewModel.updateUiState(
                        recordUiState.value.copy(
                            deleteDialogVisible = false
                        )
                    )
                },
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteRecord()
                        navigateBack()
                    }
                }
            )
        }
    }
}

@Composable
fun PaymentCard(
    modifier: Modifier = Modifier,
    payment: Payment,
    currencySymbol: String?,
    onCardClick: (Long, Long) -> Unit,
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
                onClick = { onCardClick(payment.recordId, payment.id) }
            )
    ) {
        ListItem(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = { onCardClick(payment.recordId, payment.id) }),
            headlineContent = {
                Text(
                    text = payment.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Text(
                    text = dateFormatter.format(payment.date)
                )
            },
            trailingContent = {
                Text(
                    text = decimalFormat.format(payment.amount).plus(" ").plus(currencySymbol),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Composable
fun EditRecordPopUp(
    modifier: Modifier = Modifier,
    recordUiState: RecordUiState,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onValueChange: (RecordUiState) -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        text = {
            TextField(
                value = recordUiState.person,
                onValueChange = { onValueChange(recordUiState.copy(person = it)) },
                label = {
                    Text(stringResource(R.string.person_name_text))
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onConfirm() }
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm_text))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss_text))
            }
        }
    )
}