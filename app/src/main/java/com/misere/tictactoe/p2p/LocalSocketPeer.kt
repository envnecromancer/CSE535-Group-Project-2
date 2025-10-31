package com.misere.tictactoe.p2p

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * Two-emulator transport over host loopback.
 * Host listens on device TCP <port>. Client connects to 10.0.2.2:<port>.
 */
class LocalSocketPeer(
    private val port: Int,
    private val isHost: Boolean
) : PeerService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var server: ServerSocket? = null
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    private val _events = MutableSharedFlow<PeerEvent>(extraBufferCapacity = 32)
    override val events: SharedFlow<PeerEvent> get() = _events

    // ------------------- lifecycle -------------------

    override suspend fun send(message: String) {
        try {
            val w = writer ?: return
            synchronized(this) {
                w.println(message)
                w.flush()
            }
        } catch (e: Exception) {
            _events.emit(PeerEvent.Error("Send failed: ${e.message}"))
        }
    }

    override suspend fun stop() {
        try {
            socket?.close()
            reader?.close()
            writer?.close()
            server?.close()
        } catch (_: Exception) { }
        _events.emit(PeerEvent.Disconnected("Stopped"))
    }

    // ------------------- host: listen and accept -------------------
    override suspend fun startAdvertisingOrHosting(name: String) {
        withContext(Dispatchers.IO) {
            try {
                server = ServerSocket(port, 0, InetAddress.getByName("0.0.0.0"))
                _events.emit(PeerEvent.Connected("Hosting on 0.0.0.0:$port"))
                socket = server!!.accept()
                _events.emit(PeerEvent.Connected("Client connected"))

                setupStreams(socket!!)
                readLoop()
            } catch (e: Exception) {
                _events.emit(PeerEvent.Error("Host error: ${e.message}"))
            }
        }
    }

    // ------------------- client: connect to host -------------------
    override suspend fun discoverAndConnect(target: String) {
        withContext(Dispatchers.IO) {
            val hostIp = "10.0.2.2"
            try {
                // keep trying until success or timeout
                var sock: Socket? = null
                repeat(30) { attempt ->
                    try {
                        sock = Socket(InetAddress.getByName(hostIp), port)
                        _events.emit(PeerEvent.Connected("Connected on attempt ${attempt + 1}"))
                        return@withContext setupStreamsAndListen(sock!!)
                    } catch (e: Exception) {
                        delay(300)
                    }
                }
                _events.emit(PeerEvent.Error("Client error: failed to connect to $hostIp:$port"))
            } catch (e: Exception) {
                _events.emit(PeerEvent.Error("Client setup failed: ${e.message}"))
            }
        }
    }

    private suspend fun setupStreamsAndListen(sock: Socket) {
        socket = sock
        setupStreams(sock)
        readLoop()
    }

    private fun setupStreams(sock: Socket) {
        writer = PrintWriter(sock.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(sock.getInputStream()))
    }

    private fun readLoop() {
        scope.launch {
            try {
                while (true) {
                    val line = reader?.readLine() ?: break
                    _events.emit(PeerEvent.Message(line))
                }
            } catch (e: Exception) {
                _events.emit(PeerEvent.Disconnected("Read loop stopped: ${e.message}"))
            }
        }
    }
}
