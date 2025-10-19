package com.misere.tictactoe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val board: List<List<String>> = listOf(
        listOf("", "", ""),
        listOf("", "", ""),
        listOf("", "", "")
    ),
    val turn: Int = 0,
    val winner: String = "",
    val draw: Boolean = false,
    val connectionEstablished: Boolean = false,
    val reset: Boolean = false
) : Parcelable

@Parcelize
data class GameMetadata(
    val choices: List<PlayerChoice> = emptyList(),
    val miniGame: MiniGame = MiniGame()
) : Parcelable

@Parcelize
data class PlayerChoice(
    val id: String = "",
    val name: String = ""
) : Parcelable

@Parcelize
data class MiniGame(
    val player1Choice: String = "",
    val player2Choice: String = ""
) : Parcelable

@Parcelize
data class GameMessage(
    val gameState: GameState = GameState(),
    val metadata: GameMetadata = GameMetadata()
) : Parcelable

enum class Difficulty {
    EASY, MEDIUM, HARD, VS_HUMAN
}

enum class GameMode {
    VS_AI, VS_HUMAN_ON_DEVICE, VS_HUMAN_P2P
}

enum class Player {
    X, O, NONE
}

data class GameResult(
    val id: Long = 0,
    val dateTime: Long = System.currentTimeMillis(),
    val winner: String = "",
    val difficulty: String = "",
    val gameMode: String = "",
    val isDraw: Boolean = false
)
