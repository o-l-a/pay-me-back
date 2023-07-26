package com.example.paymeback.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.paymeback.ui.screens.EditPaymentScreen
import com.example.paymeback.ui.screens.HomeDestination
import com.example.paymeback.ui.screens.HomeScreen
import com.example.paymeback.ui.screens.HomeViewModel
import com.example.paymeback.ui.screens.PaymentEditDestination
import com.example.paymeback.ui.screens.PaymentEditViewModel
import com.example.paymeback.ui.screens.RecordEditDestination
import com.example.paymeback.ui.screens.RecordEditScreen
import com.example.paymeback.ui.screens.RecordEditViewModel

const val HOME_ROUTE: String = "home"
const val EDIT_PAYMENT_ROUTE: String = "payment_edit"
const val RECORD_EDIT_ROUTE: String = "record_edit"
const val ENTRY_ID: Int = -1

@Composable
fun PayMeBackNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeDestination.route
    ) {
        composable(
            route = HomeDestination.route
        ) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                navigateToRecordEdit = { navController.navigate("${RecordEditDestination.route}/${it}") },
                viewModel = homeViewModel
            )
        }
        composable(
            route = RecordEditDestination.routeWithArgs,
            arguments = listOf(navArgument(RecordEditDestination.recordIdArg) {
                type = NavType.IntType
            })
        ) {
            val recordEditViewModel = hiltViewModel<RecordEditViewModel>()
            RecordEditScreen(
                viewModel = recordEditViewModel,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToPaymentEdit = { navController.navigate("${PaymentEditDestination.route}/$it") }
            )
        }
        composable(
            route = PaymentEditDestination.routeWithArgs,
            arguments = listOf(navArgument(PaymentEditDestination.paymentIdArg) {
                type = NavType.IntType
            })
        ) {
            val paymentEditViewModel = hiltViewModel<PaymentEditViewModel>()
            EditPaymentScreen(
                viewModel = paymentEditViewModel,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
