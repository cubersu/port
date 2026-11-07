package com.example.port.data.repository

import com.example.port.domain.model.PortEntity
import com.example.port.domain.repository.PortRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DefaultPortRepository : PortRepository {
    
    override suspend fun scanIp(ip: String, ports: List<Int>): List<PortEntity> {
        return withContext(Dispatchers.IO) {
            ports.map { port -> scanPort(ip, port) }.filterNotNull()
        }
    }
    
    override suspend fun scanPort(ip: String, port: Int): PortEntity? {
        return withContext(Dispatchers.IO) {
            try {
                val response = socketConnection(ip, port)
                parsePortResponse(response, port)
            } catch (e: Exception) {
                PortEntity(
                    portNumber = port,
                    portName = "Port-$port",
                    state = PortEntity.CLOSED,
                    osInfo = "Connection failed: ${e.message}"
                )
            }
        }
    }
    
    override suspend fun getPortDetails(port: PortEntity): PortEntity {
        // Placeholder - gerçek implementasyonda nmap veya benzeri bir araç kullanabiliriz
        return port.copy(
            state = PortEntity.OPEN,
            osInfo = "Port is open"
        )
    }
    
    private suspend fun socketConnection(ip: String, port: Int): String {
        return withContext(Dispatchers.IO) {
            val socket = java.net.InetSocketAddress(ip, port)
            val client = java.net.Socket()
            
            try {
                client.connect(socket, 3000)
                
                // TCP SYN flag gönder (basit bağlantı testi)
                val output = java.io.BufferedOutputStream(client.getOutputStream())
                val input = java.io.BufferedReader(
                    java.io.InputStreamReader(java.util.Scanner(client.getInputStream(), "UTF-8"))
                )
                
                // Basit bir SYN paketini temsil eden veri gönder
                output.write("SYN"..toByteArray())
                output.flush()
                
                val response = input.readLine()
                input.close()
                
                if (response != null) {
                    response
                } else {
                    "SYN"
                }
            } catch (e: java.net.ConnectException) {
                throw e
            } finally {
                try { client.close() } catch (_: Exception) {}
            }
        }
    }
    
    private fun parsePortResponse(response: String, port: Int): PortEntity {
        // Basit bir parse - gerçek implementasyonda OSI bilgilerini çıkarabiliriz
        return PortEntity(
            portNumber = port,
            portName = "Port-$port",
            state = PortEntity.OPEN,
            osInfo = "Response: $response"
        )
    }
}
