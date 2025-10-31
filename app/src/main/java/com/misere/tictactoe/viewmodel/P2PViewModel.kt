package com.misere.tictactoe.viewmodel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.misere.tictactoe.p2p.BluetoothPeer
import com.misere.tictactoe.p2p.PeerEvent
import com.misere.tictactoe.p2p.PeerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class P2PViewModel(application: Application) : AndroidViewModel(application) {

    private var peer: PeerService? = null

    var gameViewModelRef: GameViewModel? = null

    private val _status = MutableStateFlow("Idle")
    val status: StateFlow<String> = _status

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun startBluetoothHost(adapter: BluetoothAdapter) {
        viewModelScope.launch {
            peer?.stop()
            val p = BluetoothPeer(adapter)
            peer = p
            startCollectingEvents(p)

            _status.value = "Hosting via Bluetooth..."
            Log.d("P2PViewModel", "startBluetoothHost() called")

            launch {
                p.startAdvertisingOrHosting("BT-HOST")
            }
        }
    }

    fun connectBluetooth(adapter: BluetoothAdapter, targetName: String) {
        viewModelScope.launch {
            peer?.stop()
            val p = BluetoothPeer(adapter)
            peer = p
            startCollectingEvents(p)

            _status.value = "Searching for host..."
            Log.d("P2PViewModel", "connectBluetooth() called")

            launch {
                p.discoverAndConnect(targetName)
            }
        }
    }

    fun send(message: String) {
        viewModelScope.launch {
            Log.d("P2PViewModel", "send() -> $message")
            peer?.send(message)
        }
    }

    fun stop() {
        viewModelScope.launch {
            peer?.stop()
            _connected.value = false
            _status.value = "Idle"
        }
    }

    private fun startCollectingEvents(p: PeerService) {
        ioScope.launch {
            _status.value = "Listening for peer events..."
            Log.d("P2PViewModel", "startCollectingEvents() started")
            p.events.collectLatest { ev ->
                Log.d("P2PViewModel", "event: $ev")
                when (ev) {
                    is PeerEvent.Connected -> {
                        _connected.value = true
                        _status.value = "Connected to ${ev.peerName}"
                    }
                    is PeerEvent.Message -> {
                        val raw = ev.json.trim()
                        _status.value = "Received: $raw"
                        Log.d("P2PViewModel", "PeerEvent.Message: $raw")

                        val coords = raw.split(",")
                        if (coords.size == 2) {
                            val row = coords[0].trim().toIntOrNull()
                            val col = coords[1].trim().toIntOrNull()
                            if (row != null && col != null) {
                                gameViewModelRef?.onRemoteMoveReceived(row, col)
                            }
                        }
                    }
                    is PeerEvent.Disconnected -> {
                        _connected.value = false
                        _status.value = "Disconnected: ${ev.reason}"
                    }
                    is PeerEvent.Error -> {
                        _status.value = "Error: ${ev.message}"
                    }
                }
            }
        }
    }
}
