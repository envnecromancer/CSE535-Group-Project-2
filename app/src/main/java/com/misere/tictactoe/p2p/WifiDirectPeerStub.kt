package com.misere.tictactoe.p2p

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WifiDirectPeerStub : PeerService {
    private val _events = MutableSharedFlow<PeerEvent>(extraBufferCapacity = 64)
    override val events: Flow<PeerEvent> = _events.asSharedFlow()

    override suspend fun startAdvertisingOrHosting(name: String) {
        _events.emit(PeerEvent.Error("Wi‑Fi Direct stub not implemented on emulator."))
    }
    override suspend fun discoverAndConnect(target: String) {
        _events.emit(PeerEvent.Error("Wi‑Fi Direct stub not implemented on emulator."))
    }
    override suspend fun send(json: String) { /* no-op */ }
    override suspend fun stop() {}
}
