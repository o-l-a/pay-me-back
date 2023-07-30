package com.example.paymeback.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.paymeback.DeleteAlertDialog
import com.example.paymeback.PayMeBackBottomSaveBar
import com.example.paymeback.PayMeBackTopAppBar
import com.example.paymeback.R
import com.example.paymeback.ui.navigation.EDIT_PAYMENT_ROUTE
import com.example.paymeback.ui.navigation.NavigationDestination
import com.example.paymeback.ui.theme.spacing
import kotlinx.coroutines.launch
import java.util.Date

object PaymentEditDestination : NavigationDestination {
    override val route: String = EDIT_PAYMENT_ROUTE
    override val titleRes: Int = R.string.payment_screen_title
    const val recordIdArg = "recordId"
    const val paymentIdArg = "paymentId"
    val routeWithArgs = "$route/{$recordIdArg}/{$paymentIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: PaymentEditViewModel
) {
    val paymentUiState = viewModel.paymentUiState
    val coroutineScope = rememberCoroutineScope()
    val datePickerState = rememberDatePickerState()
    Scaffold(
        topBar = {
            PayMeBackTopAppBar(
                title = stringResource(R.string.payment_screen_title),
                navigateUp = onNavigateUp,
                canNavigateBack = true,
                hasDeleteAction = true,
                onDelete = { viewModel.updateUiState(paymentUiState.copy(deleteDialogVisible = true)) },
            )
        },
        bottomBar = {
            PayMeBackBottomSaveBar(
                onCancelClicked = navigateBack,
                onSaveClicked = {
                    coroutineScope.launch {
                        if (paymentUiState.isFirstTimeEntry) {
                            viewModel.savePayment()
                        } else {
                            viewModel.updatePayment()
                        }
                    }
                    navigateBack()
                },
                isFormValid = paymentUiState.isFormValid
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.small,
                        bottom = MaterialTheme.spacing.small
                    )
                    .fillMaxWidth()
            ) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.shape(position = 0, count = 2),
                    icon = {
                        SegmentedButtonDefaults.SegmentedButtonIcon(active = paymentUiState.isMyPayment) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    },
                    onCheckedChange = {
                        viewModel.updateUiState(paymentUiState.copy(isMyPayment = true))
                    },
                    checked = paymentUiState.isMyPayment
                ) {
                    Text(stringResource(R.string.i_paid_text))
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.shape(position = 1, count = 2),
                    icon = {
                        SegmentedButtonDefaults.SegmentedButtonIcon(active = !paymentUiState.isMyPayment) {
                            Icon(
                                imageVector = Icons.Filled.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    },
                    onCheckedChange = {
                        viewModel.updateUiState(paymentUiState.copy(isMyPayment = false))
                    },
                    checked = !paymentUiState.isMyPayment
                ) {
                    Text(stringResource(R.string.they_paid_text))
                }
            }
            OutlinedTextField(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.small,
                        bottom = MaterialTheme.spacing.small
                    )
                    .fillMaxWidth(),
                value = paymentUiState.title,
                onValueChange = { viewModel.updateUiState(paymentUiState.copy(title = it)) },
                label = { Text(stringResource(R.string.payment_title_text)) }
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.small,
                        bottom = MaterialTheme.spacing.small
                    )
                    .fillMaxWidth(),
                value = paymentUiState.amount,
                onValueChange = { viewModel.updateUiState(paymentUiState.copy(amount = it)) },
                label = { Text(stringResource(R.string.payment_amount_text)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.small,
                        bottom = MaterialTheme.spacing.small
                    )
                    .fillMaxWidth(),
                value = dateFormatter.format(paymentUiState.date),
                readOnly = true,
                onValueChange = { },
                label = { Text(stringResource(R.string.payment_date_text)) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.updateUiState(paymentUiState.copy(datePickerDialogVisible = true))
                        }
                    ) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = stringResource(R.string.payment_date_text)
                        )
                    }
                }
            )
        }
        if (paymentUiState.datePickerDialogVisible) {
            DatePickerDialog(
                onDismissRequest = {
                    viewModel.updateUiState(paymentUiState.copy(datePickerDialogVisible = false))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateUiState(
                                paymentUiState.copy(
                                    date = Date(datePickerState.selectedDateMillis!!),
                                    datePickerDialogVisible = false
                                )
                            )
                        },
                    ) {
                        Text(stringResource(R.string.ok_text))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateUiState(paymentUiState.copy(datePickerDialogVisible = false))
                        }
                    ) {
                        Text(stringResource(R.string.dismiss_text))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
    if (paymentUiState.deleteDialogVisible) {
        DeleteAlertDialog(
            onDismiss = {
                viewModel.updateUiState(
                    paymentUiState.copy(
                        deleteDialogVisible = false
                    )
                )
            },
            onConfirm = {
                coroutineScope.launch {
                    viewModel.deletePayment()
                    navigateBack()
                }
            }
        )
    }
}
