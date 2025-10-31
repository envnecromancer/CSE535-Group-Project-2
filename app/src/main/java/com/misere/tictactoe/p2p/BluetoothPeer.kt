package com.misere.tictactoe.p2p

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.IOException
import java.util.UUID

/**
 * Classic Bluetooth RFCOMM peer that implements PeerService.
 * Uses a fixed UUID so host and client can find each other.
 */
class BluetoothPeer(
    private val adapter: BluetoothAdapter
) : PeerService {

    companion object {
        private val SERVICE_UUID: UUID =
            UUID.fromString("9a7b1c1e-6c8a-4d54-98ba-a2c5a2dd6e10")
        private const val SERVICE_NAME = "MisereTTT"
        private const val TAG = "BluetoothPeer"
    }

    private val eventsFlow = MutableSharedFlow<PeerEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<PeerEvent> = eventsFlow

    private var scope: CoroutineScope? = null
    private var serverSocket: BluetoothServerSocket? = null
    private var socket: BluetoothSocket? = null
    private var writer: PrintWriter? = null

    @SuppressLint("MissingPermission")
    override suspend fun startAdvertisingOrHosting(name: String) {
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope?.launch {
            try {
                // Accept loop (one connection)
                val server = adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID)
                serverSocket = server
                val incoming = server.accept() // blocking
                socket = incoming
                writer = PrintWriter(incoming.outputStream, true)
                eventsFlow.emit(PeerEvent.Connected(incoming.remoteDevice?.name ?: "Unknown"))
                readLoop(incoming)
            } catch (e: IOException) {
                eventsFlow.tryEmit(PeerEvent.Error("Host failed: ${e.message}"))
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun discoverAndConnect(target: String) {
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope?.launch {
            try {
                val device: BluetoothDevice? = adapter.bondedDevices.firstOrNull {
                    it.name == target || it.address == target
                }
                if (device == null) {
                    eventsFlow.emit(PeerEvent.Error("Paired device not found: $target"))
                    return@launch
                }
                val sock = device.createRfcommSocketToServiceRecord(SERVICE_UUID)
                adapter.cancelDiscovery()
                sock.connect()
                socket = sock
                writer = PrintWriter(sock.outputStream, true)
                eventsFlow.emit(PeerEvent.Connected(device.name ?: "Unknown"))
                readLoop(sock)
            } catch (e: IOException) {
                eventsFlow.tryEmit(PeerEvent.Error("Client failed: ${e.message}"))
            }
        }
    }

    private fun readLoop(sock: BluetoothSocket) {
        scope?.launch {
            try {
                val reader = BufferedReader(InputStreamReader(sock.inputStream))
                while (isActive) {
                    val line = reader.readLine() ?: break
                    eventsFlow.emit(PeerEvent.Message(line))
                }
            } catch (e: Exception) {
                eventsFlow.tryEmit(PeerEvent.Disconnected("Read loop ended: ${e.message}"))
            } finally {
                stopInternal()
            }
        }
    }

    override suspend fun send(json: String) {
        try {
            writer?.println(json)
        } catch (e: Exception) {
            eventsFlow.tryEmit(PeerEvent.Error("Send error: ${e.message}"))
        }
    }

    override suspend fun stop() {
        stopInternal()
    }

    private fun stopInternal() {
        try { writer?.flush() } catch (_: Exception) {}
        try { writer?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
        try { serverSocket?.close() } catch (_: Exception) {}
        writer = null
        socket = null
        serverSocket = null
        scope?.cancel()
        scope = null
    }
}