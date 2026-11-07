package com.example.port.domain.repository

import com.example.port.domain.model.PortEntity
import kotlinx.coroutines.flow.Flow

interface PortRepository {
    suspend fun scanIp(ip: String, ports: List<Int>): List<PortEntity>
    suspend fun scanPort(ip: String, port: Int): PortEntity?
    suspend fun getPortDetails(port: PortEntity): PortEntity
}
