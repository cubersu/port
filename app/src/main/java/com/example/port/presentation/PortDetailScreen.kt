package com.example.port.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortDetailScreen(
    viewModel: ScanViewModel,
    portId: Int,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val port = state.results.firstOrNull { it.portNumber == portId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Port $portId") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (port == null) {
                Text("Port bulunamadı")
            } else {
                DetailRow("Host", state.ip)
                DetailRow("State", port.state.name)
                DetailRow("Service", port.service ?: port.portName)
                port.banner?.let { DetailRow("Banner", it) }
                port.osInfo?.let { DetailRow("Info", it) }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
