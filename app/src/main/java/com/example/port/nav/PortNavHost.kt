package com.example.port.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.port.presentation.PortDetailScreen
import com.example.port.presentation.PortListScreen
import com.example.port.presentation.PortScannerScreen
import com.example.port.presentation.ScanViewModel

@Composable
fun PortNavHost(navController: NavHostController) {
    // Tüm ekranlar aynı tarama durumunu paylaşsın diye Activity kapsamında tek ViewModel.
    val viewModel: ScanViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Scanner.route) {
        composable(Screen.Scanner.route) {
            PortScannerScreen(
                viewModel = viewModel,
                onScanFinished = { navController.navigate(Screen.List.route) }
            )
        }

        composable(Screen.List.route) {
            PortListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPortClick = { navController.navigate(Screen.Detail.createRoute(it)) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.Detail.ARG_PORT_ID) { type = NavType.IntType })
        ) { entry ->
            val portId = entry.arguments?.getInt(Screen.Detail.ARG_PORT_ID) ?: return@composable
            PortDetailScreen(
                viewModel = viewModel,
                portId = portId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
