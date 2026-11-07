package com.example.port.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PortEntity(
    val portNumber: Int,
    val portName: String,
    val service: String? = null,
    val version: String? = null,
    val state: PortState = PortState.CLOSED,
    val osInfo: String? = null,
    val manufacturer: String? = null,
    val deviceType: String? = null,
    val banner: String? = null
)

@Serializable
enum class PortState {
    OPEN, CLOSED, FILTERED, UNREACHABLE
}

@Serializable
data class ScanResult(
    val ip: String,
    val ports: List<PortEntity>
)

@Serializable
data class PortInfo(
    val port: Int,
    val name: String,
    val service: String? = null,
    val version: String? = null
)
