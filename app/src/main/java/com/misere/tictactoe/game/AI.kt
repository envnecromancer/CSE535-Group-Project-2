package com.misere.tictactoe.game

import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.Player
import kotlin.random.Random

class AI {
    
    fun getMove(board: List<List<String>>, difficulty: Difficulty, isPlayerX: Boolean): Pair<Int, Int> {
        val availableMoves = GameLogic.getAvailableMoves(board)
        if (availableMoves.isEmpty()) return Pair(-1, -1)
        
        return when (difficulty) {
            Difficulty.EASY -> getRandomMove(availableMoves)
            Difficulty.MEDIUM -> getMediumMove(board, availableMoves, isPlayerX)
            Difficulty.HARD -> getOptimalMove(board, availableMoves, isPlayerX)
            else -> getRandomMove(availableMoves)
        }
    }
    
    private fun getRandomMove(availableMoves: List<Pair<Int, Int>>): Pair<Int, Int> {
        return availableMoves[Random.nextInt(availableMoves.size)]
    }
    
    private fun getMediumMove(board: List<List<String>>, availableMoves: List<Pair<Int, Int>>, isPlayerX: Boolean): Pair<Int, Int> {
        return if (Random.nextBoolean()) {
            getOptimalMove(board, availableMoves, isPlayerX)
        } else {
            getRandomMove(availableMoves)
        }
    }
    
    private fun getOptimalMove(board: List<List<String>>, availableMoves: List<Pair<Int, Int>>, isPlayerX: Boolean): Pair<Int, Int> {
        var bestMove = availableMoves[0]
        var bestScore = Int.MIN_VALUE
        
        for (move in availableMoves) {
            val newBoard = makeMove(board, move, if (isPlayerX) "O" else "X")
            val score = minimax(newBoard, 0, false, isPlayerX, Int.MIN_VALUE, Int.MAX_VALUE)
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        
        return bestMove
    }
    
    private fun minimax(board: List<List<String>>, depth: Int, isMaximizing: Boolean, isPlayerX: Boolean, alpha: Int, beta: Int): Int {
        val winner = GameLogic.checkWinner(board)
        
        if (winner != Player.NONE) {
            // In Misere Tic-Tac-Toe, the player who completes a line LOSES
            return if (isMaximizing) -1000 + depth else 1000 - depth
        }
        
        if (GameLogic.isBoardFull(board)) {
            return 0 // Draw
        }
        
        val availableMoves = GameLogic.getAvailableMoves(board)
        if (availableMoves.isEmpty()) return 0
        
        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            var newAlpha = alpha
            
            for (move in availableMoves) {
                val newBoard = makeMove(board, move, if (isPlayerX) "O" else "X")
                val eval = minimax(newBoard, depth + 1, false, isPlayerX, newAlpha, beta)
                maxEval = maxOf(maxEval, eval)
                newAlpha = maxOf(newAlpha, eval)
                
                if (beta <= newAlpha) break // Alpha-beta pruning
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            var newBeta = beta
            
            for (move in availableMoves) {
                val newBoard = makeMove(board, move, if (isPlayerX) "X" else "O")
                val eval = minimax(newBoard, depth + 1, true, isPlayerX, alpha, newBeta)
                minEval = minOf(minEval, eval)
                newBeta = minOf(newBeta, eval)
                
                if (newBeta <= alpha) break // Alpha-beta pruning
            }
            return minEval
        }
    }
    
    private fun makeMove(board: List<List<String>>, move: Pair<Int, Int>, symbol: String): List<List<String>> {
        val newBoard = board.map { it.toMutableList() }.toMutableList()
        newBoard[move.first][move.second] = symbol
        return newBoard
    }
}
