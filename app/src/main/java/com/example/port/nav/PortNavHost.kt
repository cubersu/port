package com.example.port.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navData
import androidx.navigation.NavRoute.Companion.getArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Scanner : Screen("scanner")
    object List : Screen("list")
    object Detail : Screen("detail/{portId}")
    
    companion object {
        fun Scanner() = Screen.Scanner
        fun List() = Screen.List
        fun Detail(portId: Int) = Screen.Detail(portId)
    }
}

@Composable
fun PortNavHost(
    navController: NavHostController,
    portRepository: PortRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scanner.route
    ) {
        composable(Screen.Scanner.route) {
            PortScannerScreen(
                onScan = { ip, ports ->
                    navController.navigate(Screen.List.route)
                }
            )
        }
        
        composable(Screen.List.route) {
            PortListScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navData("portId", NavType.IntType)
            )
        ) { backStackEntry ->
            val portId = backStackEntry.getArgument("portId") ?: return@composable
            PortDetailScreen(
                portId = portId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
