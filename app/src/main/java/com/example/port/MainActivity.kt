package com.example.port

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.port.domain.repository.PortRepository
import com.example.port.presentation.PortNavHost
import com.example.port.presentation.PortScannerScreen

class MainActivity : ComponentActivity() {
    private lateinit var portRepository: PortRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        portRepository = DefaultPortRepository()
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    PortNavHost(
                        navController = navController,
                        portRepository = portRepository
                    )
                }
            }
        }
    }
}
