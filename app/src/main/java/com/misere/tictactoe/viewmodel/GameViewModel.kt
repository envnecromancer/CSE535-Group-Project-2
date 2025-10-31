package com.misere.tictactoe.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misere.tictactoe.data.AppDatabase
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode
import com.misere.tictactoe.data.GameResult
import com.misere.tictactoe.data.GameState
import com.misere.tictactoe.data.Player
import kotlinx.coroutines.Dispatchers
import com.misere.tictactoe.data.toGameResultEntity
import com.misere.tictactoe.game.AI
import com.misere.tictactoe.game.GameLogic
import com.misere.tictactoe.repository.GameRepository
import com.misere.tictactoe.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val ai = AI()

    private val gameRepository = GameRepository.getInstance(application)
    private val settingsRepository = SettingsRepository.getInstance(application)

    // Settings
    private val _difficulty = MutableLiveData(getDifficultyFromPrefs())
    val difficulty: LiveData<Difficulty> = _difficulty

    // board / rules
    private val _gameState = MutableLiveData(GameState())
    val gameState: LiveData<GameState> = _gameState

    private val _gameMode = MutableLiveData(GameMode.PLAYER_VS_BOT)
    val gameMode: LiveData<GameMode> = _gameMode

    private val _isThinking = MutableLiveData(false)
    val isThinking: LiveData<Boolean> = _isThinking

    // Game results
    private val _gameResults = MutableLiveData<List<GameResult>>(emptyList())
    val gameResults: LiveData<List<GameResult>> = _gameResults

    init {
        loadGameResults()
    }

    // db
    private val dao = AppDatabase.getDatabase(getApplication()).gameResultDao()
//    val gameResults = dao.getAllGameResults().asLiveData()

    // link to P2P
    var p2pViewModel: P2PViewModel? = null

    // Get difficulty from repository
    private fun getDifficultyFromPrefs(): Difficulty {
        return settingsRepository.getDifficulty()
    }

    // Get game mode from repository
    private fun getGameModeFromPrefs(): GameMode {
        return settingsRepository.getGameMode()
    }

    // Save difficulty
    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.value = difficulty
        settingsRepository.saveDifficulty(difficulty)
    }

    // Save game mode
    fun setGameMode(gameMode: GameMode) {
        _gameMode.value = gameMode
        settingsRepository.saveGameMode(gameMode)
        resetGame()
    }


    private fun recordResultIfOver(newState: GameState) {
        val winner = newState.winner
        val draw = newState.draw
        if (winner.isEmpty() && !draw) return

        val result = GameResult(
            winner = winner,
            difficulty = _difficulty.value?.name ?: "",
            gameMode = _gameMode.value?.name ?: "",
            isDraw = draw
        )

        viewModelScope.launch {
            dao.insertGameResult(result.toGameResultEntity())
        }
    }

    // Player makes a move
    fun makeMove(row: Int, col: Int) {
        val currentState = _gameState.value ?: return
        val currentBoard = currentState.board

        // Check if cell is empty and game is not over
        if (currentBoard[row][col] != "" || currentState.winner != "" || currentState.draw) {
            return
        }

        // Determine which symbol to place based on game mode and turn
        val symbol = when (_gameMode.value) {
            GameMode.PLAYER_VS_BOT -> "X" // Human always plays X against AI
            else -> {
                // Alternating symbols between X and O based on turn number
                if (currentState.turn % 2 == 0) "X" else "O"
            }
        }

        // Making the move
        val newBoard = currentBoard.map { it.toMutableList() }.toMutableList()
        newBoard[row][col] = symbol

        val newState = currentState.copy(
            board = newBoard,
            turn = currentState.turn + 1
        )

        _gameState.value = newState

        // Send move to P2P peer if in P2P mode
        if (_gameMode.value == GameMode.PLAYER_VS_PLAYER_P2P) {
            Log.d("GameViewModel", "Sending P2P move: $row,$col")
            p2pViewModel?.send("$row,$col")
        }

        // Checking for game over and storing result
        val gameEnded = checkGameEnd(newState)

        // If playing against AI and game isn't over, make AI move
        if (_gameMode.value == GameMode.PLAYER_VS_BOT && !gameEnded) {
            makeAIMove(newState)
        }
    }

    // AI makes a move
    private fun makeAIMove(currentState: GameState) {
        viewModelScope.launch {
            _isThinking.value = true

            // Adding delay to show AI thinking
            delay(1000)

            val currentBoard = currentState.board
            val aiMove = ai.getMove(currentBoard, _difficulty.value ?: Difficulty.EASY, true)

            if (aiMove.first != -1 && aiMove.second != -1) {
                val newBoard = currentBoard.map { it.toMutableList() }.toMutableList()
                newBoard[aiMove.first][aiMove.second] = "O"

                val newState = currentState.copy(
                    board = newBoard,
                    turn = currentState.turn + 1
                )

                _gameState.value = newState
                checkGameEnd(newState)
            }

            _isThinking.value = false
        }
    }


    // Check if game ended - returns true if game is over
    private fun checkGameEnd(state: GameState): Boolean {
        val loser = GameLogic.checkWinner(state.board)
        val isDraw = GameLogic.isBoardFull(state.board) && loser == Player.NONE

        if (loser != Player.NONE || isDraw) {
            val actualWinner = when (loser) {
                Player.X -> "O"
                Player.O -> "X"
                else -> ""
            }

            val finalState = state.copy(
                winner = actualWinner,
                draw = isDraw
            )

            _gameState.value = finalState
            saveGameResult(finalState)
            return true
        }
        return false
    }

    // Save game result to repository
    private fun saveGameResult(state: GameState) {
        viewModelScope.launch {
            val gameResult = GameResult(
                id = 0,
                dateTime = System.currentTimeMillis(),
                winner = if (state.draw) "Draw" else state.winner,
                difficulty = _difficulty.value?.name ?: "EASY",
                gameMode = _gameMode.value?.name ?: "PLAYER_VS_BOT",
                isDraw = state.draw
            )
            gameRepository.insertGameResult(gameResult)
            loadGameResults()
        }
    }

    // Load game results from repository
    private fun loadGameResults() {
        viewModelScope.launch {
            val results = gameRepository.getAllGameResults()
            _gameResults.postValue(results)
        }
    }

    // Delete all game results
    fun deleteAllGameResults() {
        viewModelScope.launch {
            gameRepository.deleteAllGameResults()
            loadGameResults()
        }
    }

    // Check if game is over
    private fun isGameOver(state: GameState): Boolean {
        return state.winner != "" || state.draw
    }

    // Reset game
    fun resetGame() {
        _gameState.value = GameState()
        _isThinking.value = false
    }


    fun onRemoteMoveReceived(row: Int, col: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            Log.d("GameViewModel", "Received remote P2P move: $row,$col")
            val cur = _gameState.value ?: return@launch
            val board = cur.board.map { it.toMutableList() }
            
            if (board[row][col].isNotEmpty()) {
                Log.w("GameViewModel", "Cell already occupied at $row,$col")
                return@launch
            }

            val symbol = if (cur.turn % 2 == 0) "X" else "O"
            board[row][col] = symbol
            val next = cur.copy(board = board, turn = cur.turn + 1)

            val finished = checkGameFinished(next)
            _gameState.value = finished
            
            // Use saveGameResult instead of recordResultIfOver to refresh UI
            if (finished.winner.isNotEmpty() || finished.draw) {
                saveGameResult(finished)
            }
        }
    }

    private fun checkGameFinished(state: GameState): GameState {
        val b = state.board

        // standard TicTacToe line check: if someone completes 3-in-a-row they LOSE,
        // so winner is the OTHER symbol.
        val lines = listOf(
            listOf(b[0][0], b[0][1], b[0][2]),
            listOf(b[1][0], b[1][1], b[1][2]),
            listOf(b[2][0], b[2][1], b[2][2]),
            listOf(b[0][0], b[1][0], b[2][0]),
            listOf(b[0][1], b[1][1], b[2][1]),
            listOf(b[0][2], b[1][2], b[2][2]),
            listOf(b[0][0], b[1][1], b[2][2]),
            listOf(b[0][2], b[1][1], b[2][0])
        )

        var loser: String? = null
        for (line in lines) {
            if (line[0].isNotEmpty() && line[0] == line[1] && line[1] == line[2]) {
                loser = line[0]
                break
            }
        }

        if (loser != null) {
            val winner = if (loser == "X") "O" else "X"
            return state.copy(winner = winner, draw = false)
        }

        val allFilled = b.all { row -> row.all { it.isNotEmpty() } }
        if (allFilled) {
            return state.copy(winner = "", draw = true)
        }

        return state
    }

    private fun aiMove() {
        viewModelScope.launch(Dispatchers.Main) {
            _isThinking.value = true
            val delayMs = when (_difficulty.value) {
                Difficulty.EASY -> 400L
                Difficulty.MEDIUM -> 800L
                Difficulty.HARD -> 1200L
                else -> 600L
            }
            delay(delayMs)

            val cur = _gameState.value ?: return@launch
            if (cur.winner.isNotEmpty() || cur.draw) {
                _isThinking.value = false
                return@launch
            }

            val board = cur.board.map { it.toMutableList() }
            var placed = false
            outer@ for (r in 0 until 3) {
                for (c in 0 until 3) {
                    if (board[r][c].isEmpty()) {
                        board[r][c] = if (cur.turn % 2 == 0) "X" else "O"
                        placed = true
                        break@outer
                    }
                }
            }

            if (placed) {
                val next = cur.copy(board = board, turn = cur.turn + 1)
                val finished = checkGameFinished(next)
                _gameState.value = finished
                recordResultIfOver(finished)
            }

            _isThinking.value = false
        }
    }
}
