package com.misere.tictactoe.p2p

import kotlinx.coroutines.flow.Flow

sealed interface PeerEvent {
    data class Connected(val peerName: String): PeerEvent
    data class Disconnected(val reason: String): PeerEvent
    data class Message(val json: String): PeerEvent
    data class Error(val message: String): PeerEvent
}

interface PeerService {
    val events: Flow<PeerEvent>
    suspend fun startAdvertisingOrHosting(name: String)
    suspend fun discoverAndConnect(target: String)
    suspend fun send(json: String)
    suspend fun stop()
}
