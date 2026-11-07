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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortScannerScreen(
    onScan: (String, List<Int>) -> Unit,
    portRepository: PortRepository
) {
    var targetIp by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var scanResults by remember { mutableStateOf<List<PortEntity>>(emptyList()) }
    var selectedPorts by remember { mutableStateOf(listOf(22, 80, 443, 8080)) }
    var showPortDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Port listesini yükle
    }
    
    LaunchedEffect(targetIp, selectedPorts) {
        if (targetIp.isNotEmpty() && selectedPorts.isNotEmpty() && !isScanning) {
            isScanning = true
            val results = portRepository.scanIp(targetIp, selectedPorts)
            scanResults = results
            isScanning = false
            onScan(targetIp, selectedPorts)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Port Scanner") },
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
            // IP Adresi Girişi
            OutlinedTextField(
                value = targetIp,
                onValueChange = { 
                    targetIp = it
                    if (it.isNotEmpty()) {
                        scanResults = emptyList()
                    }
                },
                label = { Text("Target IP Address") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("192.168.1.1") },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Port Seçimi
            Text(
                text = "Select Ports",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Port listesi
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                content = {
                    items(
                        items = listOf(22, 80, 443, 8080, 3389, 8443, 27017, 5432),
                        key = { it }
                    ) { port ->
                        Checkbox(
                            checked = selectedPorts.contains(port),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedPorts += port
                                } else {
                                    selectedPorts = selectedPorts.filter { p -> p != port }
                                }
                            }
                        ) {
                            Text("Port $port")
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scan Butonu
            Button(
                onClick = {
                    if (targetIp.isNotEmpty() && selectedPorts.isNotEmpty()) {
                        isScanning = true
                        scanResults = emptyList()
                        onScan(targetIp, selectedPorts)
                    }
                },
                enabled = targetIp.isNotEmpty() && selectedPorts.isNotEmpty() && !isScanning,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(if (isScanning) "Scanning..." else "Start Scan")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sonuçlar
            if (scanResults.isNotEmpty()) {
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
                            items = scanResults.take(10), // İlk 10 sonucu göster
                            key = { it.portNumber }
                        ) { port ->
                            PortItem(
                                port = port,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        // Detay göster
                                    }
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
