package com.example.port.nav

sealed class Screen(val route: String) {
    data object Scanner : Screen("scanner")
    data object List : Screen("list")
    data object Detail : Screen("detail/{portId}") {
        const val ARG_PORT_ID = "portId"
        fun createRoute(portId: Int) = "detail/$portId"
    }
}
