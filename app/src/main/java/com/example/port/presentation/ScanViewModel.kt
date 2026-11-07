package com.example.port.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.port.di.PortModule
import com.example.port.domain.model.PortEntity
import com.example.port.domain.repository.PortRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val SELECTABLE_PORTS = listOf(22, 80, 443, 8080, 3389, 8443, 27017, 5432)

data class ScanUiState(
    val ip: String = "",
    val selectedPorts: Set<Int> = setOf(22, 80, 443, 8080),
    val isScanning: Boolean = false,
    val results: List<PortEntity> = emptyList(),
    val error: String? = null
) {
    val canScan: Boolean get() = ip.isNotBlank() && selectedPorts.isNotEmpty() && !isScanning
}

class ScanViewModel(
    private val repository: PortRepository = PortModule.portRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScanUiState())
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    private val _scanFinished = Channel<Unit>(Channel.BUFFERED)
    /** Tarama bittiğinde bir kez tetiklenir; ekran sonuç listesine geçmek için dinler. */
    val scanFinished = _scanFinished.receiveAsFlow()

    fun onIpChange(value: String) = _state.update { it.copy(ip = value.trim(), error = null) }

    fun onTogglePort(port: Int, checked: Boolean) = _state.update {
        it.copy(selectedPorts = if (checked) it.selectedPorts + port else it.selectedPorts - port)
    }

    fun scan() {
        val current = _state.value
        if (!current.canScan) return
        _state.update { it.copy(isScanning = true, results = emptyList(), error = null) }
        viewModelScope.launch {
            try {
                val results = repository.scanIp(current.ip, current.selectedPorts.toList())
                _state.update { it.copy(isScanning = false, results = results) }
                _scanFinished.send(Unit)
            } catch (e: Exception) {
                _state.update { it.copy(isScanning = false, error = e.message ?: "Tarama başarısız") }
            }
        }
    }
}
