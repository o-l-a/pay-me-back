package com.example.paymeback.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.paymeback.ui.screens.EditPaymentDestination
import com.example.paymeback.ui.screens.EditPaymentScreen
import com.example.paymeback.ui.screens.EditPaymentViewModel
import com.example.paymeback.ui.screens.HomeDestination
import com.example.paymeback.ui.screens.HomeScreen
import com.example.paymeback.ui.screens.HomeViewModel

const val HOME_ROUTE: String = "home"
const val EDIT_PAYMENT_ROUTE: String = "edit_payment"

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
            route = HomeDestination.route,
            arguments = listOf(navArgument(HomeDestination.recordIdArg) {
                type = NavType.IntType
            })
        ) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = homeViewModel,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToPaymentEdit = { navController.navigate("${EditPaymentDestination.route}/$it") }
            )
        }
        composable(
            route = EditPaymentDestination.routeWithArgs,
            arguments = listOf(navArgument(EditPaymentDestination.paymentIdArg) {
                type = NavType.IntType
            })
        ) {
            val editPaymentViewModel = hiltViewModel<EditPaymentViewModel>()
            EditPaymentScreen(
                viewModel = editPaymentViewModel,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
