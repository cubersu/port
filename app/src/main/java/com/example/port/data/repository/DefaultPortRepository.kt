package com.example.port.data.repository

import com.example.port.domain.model.PortEntity
import com.example.port.domain.model.PortState
import com.example.port.domain.repository.PortRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.NoRouteToHostException
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** TCP connect taraması yapan repository. */
class DefaultPortRepository : PortRepository {

    override suspend fun scanIp(ip: String, ports: List<Int>): List<PortEntity> =
        coroutineScope {
            ports.distinct().map { port -> async { scanPort(ip, port) } }
                .awaitAll()
                .filterNotNull()
                .sortedBy { it.portNumber }
        }

    override suspend fun scanPort(ip: String, port: Int): PortEntity? =
        withContext(Dispatchers.IO) {
            val name = WELL_KNOWN_PORTS[port] ?: "Port-$port"
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(ip, port), CONNECT_TIMEOUT_MS)
                    PortEntity(
                        portNumber = port,
                        portName = name,
                        service = WELL_KNOWN_PORTS[port],
                        state = PortState.OPEN,
                        banner = readBanner(socket)
                    )
                }
            } catch (e: ConnectException) {
                // Bağlantı reddedildi: host ayakta, port kapalı.
                PortEntity(portNumber = port, portName = name, state = PortState.CLOSED)
            } catch (e: SocketTimeoutException) {
                // Yanıt yok: büyük olasılıkla güvenlik duvarı paketleri düşürüyor.
                PortEntity(portNumber = port, portName = name, state = PortState.FILTERED)
            } catch (e: UnknownHostException) {
                unreachable(port, name, "Host çözümlenemedi")
            } catch (e: NoRouteToHostException) {
                unreachable(port, name, "Host'a ulaşılamıyor")
            } catch (e: Exception) {
                unreachable(port, name, e.message)
            }
        }

    override suspend fun getPortDetails(port: PortEntity): PortEntity {
        val known = WELL_KNOWN_PORTS[port.portNumber]
        return port.copy(
            portName = known ?: port.portName,
            service = port.service ?: known
        )
    }

    private fun unreachable(port: Int, name: String, reason: String?) = PortEntity(
        portNumber = port,
        portName = name,
        state = PortState.UNREACHABLE,
        osInfo = reason
    )

    /** Sunucu kendiliğinden banner gönderiyorsa (SSH, FTP, SMTP...) kısa süre bekleyip okur. */
    private fun readBanner(socket: Socket): String? = try {
        socket.soTimeout = BANNER_TIMEOUT_MS
        val buffer = ByteArray(256)
        val read = socket.getInputStream().read(buffer)
        if (read > 0) String(buffer, 0, read, Charsets.UTF_8).trim().takeIf { it.isNotEmpty() } else null
    } catch (e: Exception) {
        null
    }

    private companion object {
        const val CONNECT_TIMEOUT_MS = 3000
        const val BANNER_TIMEOUT_MS = 500

        val WELL_KNOWN_PORTS = mapOf(
            21 to "FTP",
            22 to "SSH",
            23 to "Telnet",
            25 to "SMTP",
            53 to "DNS",
            80 to "HTTP",
            110 to "POP3",
            143 to "IMAP",
            443 to "HTTPS",
            3306 to "MySQL",
            3389 to "RDP",
            5432 to "PostgreSQL",
            8080 to "HTTP-Alt",
            8443 to "HTTPS-Alt",
            27017 to "MongoDB"
        )
    }
}
