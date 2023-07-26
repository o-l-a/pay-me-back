package com.example.paymeback.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import com.example.paymeback.PayMeBackTopAppBar
import com.example.paymeback.R
import com.example.paymeback.data.Payment
import com.example.paymeback.ui.navigation.HOME_ROUTE
import com.example.paymeback.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route: String = HOME_ROUTE
    override val titleRes: Int = R.string.home_screen_title
    const val recordIdArg = "recordId"
    val routeWithArgs = "${EditPaymentDestination.route}/{$recordIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToPaymentEdit: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: HomeViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val recordUiState = viewModel.homeUiState
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PayMeBackTopAppBar(
                title = stringResource(R.string.app_name),
                navigateUp = onNavigateUp,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        HomeBody(
            modifier = modifier.padding(innerPadding),
            record = recordUiState,
            onCardClick = navigateToPaymentEdit
        )
    }
}

@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    record: RecordUiState,
    onCardClick: (Int) -> Unit
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (record.actionEnabled) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.add_new_record))
            }
        } else {
            LazyColumn {
                item {
                    Text("add new")
                }
                items(record.myPayments) { payment ->
                    PaymentCard(payment = payment, onCardClick = onCardClick)
                }
            }
            LazyColumn {
                item {
                    Text("add new")
                }
                items(record.personsPayments) { payment ->
                    PaymentCard(payment = payment, onCardClick = onCardClick)
                }
            }
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

@Composable
fun EditRecordPopUp(
    modifier: Modifier = Modifier,
    recordUiState: RecordUiState
) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        TextField(value = recordUiState.person, onValueChange = recordUiState.person = it)
        Button(onClick = { /*TODO*/ }) {

        }
    }
}