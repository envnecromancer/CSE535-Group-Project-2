package com.misere.tictactoe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misere.tictactoe.data.*
import com.misere.tictactoe.game.AI
import com.misere.tictactoe.game.GameLogic
import com.misere.tictactoe.repository.SimpleGameRepository
import com.misere.tictactoe.repository.SimpleSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val ai = AI()
    
    // Singleton repositories - no Hilt needed
    private val gameRepository = SimpleGameRepository.getInstance(application)
    private val settingsRepository = SimpleSettingsRepository.getInstance(application)
    
    // Game state
    private val _gameState = MutableLiveData(GameState())
    val gameState: LiveData<GameState> = _gameState
    
    // Settings
    private val _difficulty = MutableLiveData(getDifficultyFromPrefs())
    val difficulty: LiveData<Difficulty> = _difficulty
    
    private val _gameMode = MutableLiveData(getGameModeFromPrefs())
    val gameMode: LiveData<GameMode> = _gameMode
    
    // AI thinking state
    private val _isThinking = MutableLiveData(false)
    val isThinking: LiveData<Boolean> = _isThinking
    
    // Game results
    private val _gameResults = MutableLiveData<List<GameResult>>(emptyList())
    val gameResults: LiveData<List<GameResult>> = _gameResults
    
    init {
        loadGameResults()
    }
    
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
            GameMode.PLAYER_VS_PLAYER_ON_DEVICE -> {
                // Alternating symbols between X and O based on turn number
                if (currentState.turn % 2 == 0) "X" else "O"
            }
            else -> "X" // Default to X for other modes
        }
        
        // Making the move
        val newBoard = currentBoard.map { it.toMutableList() }.toMutableList()
        newBoard[row][col] = symbol
        
        val newState = currentState.copy(
            board = newBoard,
            turn = currentState.turn + 1
        )
        
        _gameState.value = newState
        
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
}

