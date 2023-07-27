package com.example.paymeback.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.example.paymeback.DeleteAlertDialog
import com.example.paymeback.PayMeBackTopAppBar
import com.example.paymeback.R
import com.example.paymeback.data.Payment
import com.example.paymeback.ui.navigation.NavigationDestination
import com.example.paymeback.ui.navigation.RECORD_EDIT_ROUTE
import kotlinx.coroutines.launch

object RecordEditDestination : NavigationDestination {
    override val route: String = RECORD_EDIT_ROUTE
    override val titleRes: Int = R.string.record_screen_title
    const val recordIdArg = "recordId"
    val routeWithArgs = "${RecordEditDestination.route}/{$recordIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToPaymentEdit: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: RecordEditViewModel
) {
    val recordUiState = viewModel.recordUiState
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            PayMeBackTopAppBar(
                title = stringResource(RecordEditDestination.titleRes),
                navigateUp = onNavigateUp,
                canNavigateBack = true,
                hasAction = true,
                onDelete = { viewModel.updateUiState(recordUiState.copy(deleteDialogVisible = true)) }
            )
        }
    ) { innerPadding ->
        if (!recordUiState.isFirstTimeEntry) {
            Text(
                text = recordUiState.person,
                modifier = modifier.padding(innerPadding)
            )
        }
        if (recordUiState.actionEnabled) {
            EditRecordPopUp(
                recordUiState = recordUiState,
                onValueChange = viewModel::updateUiState,
                onDismiss = {
                    viewModel.updateUiState(recordUiState.copy(actionEnabled = false))
                    if (recordUiState.isFirstTimeEntry) {
                        navigateBack()
                    }
                },
                onConfirm = {
                    coroutineScope.launch {
                        if (recordUiState.isFirstTimeEntry) {
                            viewModel.saveRecord()
                        } else {
                            viewModel.updateRecord()
                        }
                        viewModel.updateUiState(
                            recordUiState.copy(
                                actionEnabled = false,
                                isFirstTimeEntry = false
                            )
                        )
                    }
                }
            )
        }
        if (recordUiState.deleteDialogVisible) {
            DeleteAlertDialog(
                onDismiss = {
                    viewModel.updateUiState(
                        recordUiState.copy(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCard(
    modifier: Modifier = Modifier,
    payment: Payment,
    onCardClick: (Int) -> Unit,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onCardClick(payment.id) }),
        headlineText = {
            Text(
                text = payment.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingText = {
            Text(
                text = payment.date.toString(),
            )
        },
        leadingContent = {
            Text(
                text = payment.amount.toString()
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
                supportingText = {
                    Text(stringResource(R.string.person_name_supporting_text))
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onConfirm() }
                )
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