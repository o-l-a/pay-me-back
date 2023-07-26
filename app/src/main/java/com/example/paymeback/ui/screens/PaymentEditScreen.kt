package com.example.paymeback.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.paymeback.PayMeBackTopAppBar
import com.example.paymeback.R
import com.example.paymeback.ui.navigation.EDIT_PAYMENT_ROUTE
import com.example.paymeback.ui.navigation.NavigationDestination

object PaymentEditDestination : NavigationDestination {
    override val route: String = EDIT_PAYMENT_ROUTE
    override val titleRes: Int = R.string.edit_payment_title
    const val paymentIdArg = "paymentId"
    val routeWithArgs = "$route/{$paymentIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: PaymentEditViewModel
) {
    Scaffold(
        topBar = {
            PayMeBackTopAppBar(
                title = stringResource(R.string.edit_payment_title),
                navigateUp = onNavigateUp,
                canNavigateBack = true
            )
        }
    ) { innerPadding ->
        Text(
            text = "hej",
            modifier = modifier.padding(innerPadding)
        )
    }
}