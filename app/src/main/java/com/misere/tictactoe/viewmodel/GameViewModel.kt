package com.misere.tictactoe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misere.tictactoe.data.*
import com.misere.tictactoe.game.AI
import com.misere.tictactoe.game.GameLogic
import com.misere.tictactoe.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    private val ai = AI()
    
    private val _gameState = MutableStateFlow(
        GameState()
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _difficulty = MutableStateFlow(Difficulty.EASY)
    val difficulty: StateFlow<Difficulty> = _difficulty.asStateFlow()
    
    private val _gameMode = MutableStateFlow(GameMode.VS_AI)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()
    
    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()
    
    private val _gameMessage = MutableStateFlow(GameMessage())
    val gameMessage: StateFlow<GameMessage> = _gameMessage.asStateFlow()
    
    fun makeMove(row: Int, col: Int) {
        val currentState = _gameState.value
        val currentBoard = currentState.board
        
        // Check if cell is empty and it's the player's turn
        if (currentBoard[row][col] != "" || currentState.winner != "" || currentState.draw) {
            return
        }
        
        // Make the move
        val newBoard = currentBoard.map { it.toMutableList() }.toMutableList()
        newBoard[row][col] = "X"
        
        val newState = currentState.copy(
            board = newBoard,
            turn = currentState.turn + 1
        )
        
        _gameState.value = newState
        
        // Check for game end
        checkGameEnd(newState)
        
        // If playing against AI and game isn't over, make AI move
        if (_gameMode.value == GameMode.VS_AI && !isGameOver(newState)) {
            makeAIMove(newState)
        }
    }
    
    private fun makeAIMove(currentState: GameState) {
        viewModelScope.launch {
            _isThinking.value = true
            
            // Add delay to show "Thinking..." message
            delay(1000)
            
            val currentBoard = currentState.board
            val aiMove = ai.getMove(currentBoard, _difficulty.value, true)
            
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
    
    private fun checkGameEnd(state: GameState) {
        val winner = GameLogic.checkWinner(state.board)
        val isDraw = GameLogic.isBoardFull(state.board) && winner == Player.NONE
        
        if (winner != Player.NONE || isDraw) {
            val finalState = state.copy(
                winner = if (winner != Player.NONE) winner.name else "",
                draw = isDraw
            )
            _gameState.value = finalState
            
            // Save game result
            saveGameResult(finalState)
        }
    }
    
    private fun saveGameResult(state: GameState) {
        viewModelScope.launch {
            val gameResult = GameResult(
                dateTime = System.currentTimeMillis(),
                winner = if (state.draw) "Draw" else state.winner,
                difficulty = _difficulty.value.name,
                gameMode = _gameMode.value.name,
                isDraw = state.draw
            )
            gameRepository.insertGameResult(gameResult)
        }
    }
    
    private fun isGameOver(state: GameState): Boolean {
        return state.winner != "" || state.draw
    }
    
    fun resetGame() {
        _gameState.value = GameState()
        _isThinking.value = false
    }
    
    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.value = difficulty
    }
    
    fun setGameMode(gameMode: GameMode) {
        _gameMode.value = gameMode
        resetGame()
    }
    
    fun getAllGameResults() = gameRepository.getAllGameResults()
}
