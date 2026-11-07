package com.example.port.presentation

sealed class Screen {
    object Scanner : Screen()
    object List : Screen()
}
