package com.misere.tictactoe.model

import kotlinx.serialization.Serializable

enum class Cell { EMPTY, X, O }
enum class Player { X, O }
enum class Difficulty { EASY, MEDIUM, HARD, HUMAN_LOCAL, HUMAN_P2P }

@Serializable
data class GameState(
    val board: List<List<String>>,
    val turn: Int,
    val winner: String,
    val draw: Boolean,
    val connectionEstablished: Boolean,
    val reset: Boolean
)

@Serializable
data class Metadata(
    val choices: List<Choice>,
    val miniGame: MiniGame,
    val senderId: String = ""
)

@Serializable
data class Choice(val id: String, val name: String)

@Serializable
data class MiniGame(
    val player1Choice: String,
    val player2Choice: String
)

@Serializable
data class WireEnvelope(
    val gameState: GameState,
    val metadata: Metadata
)
