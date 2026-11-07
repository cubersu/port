package com.example.port.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.port.domain.model.PortEntity
import com.example.port.domain.model.PortState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortListScreen(
    onBack: () -> Unit,
    portRepository: PortRepository
) {
    var scanResults by remember { mutableStateOf<List<PortEntity>>(emptyList()) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedPort by remember { mutableStateOf<PortEntity?>(null) }
    
    LaunchedEffect(Unit) {
        // Sonuçları yükle - bunlar global state'den gelecek
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Port List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
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
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Target IP Address") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("192.168.1.1") },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    // Scan butonu
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan Again")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (scanResults.isEmpty()) {
                Text(
                    text = "No scan results yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Scan Results",
                    style = MaterialTheme.typography.titleMedium
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    content = {
                        items(
                            items = scanResults,
                            key = { it.portNumber }
                        ) { port ->
                            PortItem(
                                port = port,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PortItem(
    port: PortEntity,
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Surface(
            modifier = modifier,
            color = when (port.state) {
                PortEntity.OPEN -> MaterialTheme.colorScheme.tertiaryContainer
                PortEntity.CLOSED -> MaterialTheme.colorScheme.secondaryContainer
                PortEntity.FILTERED -> MaterialTheme.colorScheme.primaryContainer
                PortEntity.UNREACHABLE -> MaterialTheme.colorScheme.errorContainer
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Port: $port.portNumber",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = port.state.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (port.state) {
                            PortEntity.OPEN -> MaterialTheme.colorScheme.onTertiaryContainer
                            PortEntity.CLOSED -> MaterialTheme.colorScheme.onSecondaryContainer
                            PortEntity.FILTERED -> MaterialTheme.colorScheme.onPrimaryContainer
                            PortEntity.UNREACHABLE -> MaterialTheme.colorScheme.onError
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = port.portName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
