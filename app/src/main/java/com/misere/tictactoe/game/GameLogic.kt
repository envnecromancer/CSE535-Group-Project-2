package com.misere.tictactoe.game

import com.misere.tictactoe.data.Player

class GameLogic {
    
    companion object {
        fun checkWinner(board: List<List<String>>): Player {

            for (row in board) {
                if (row.all { it == "X" } && row.none { it == "" }) {
                    return Player.X // X completed a line, so X loses
                }
                if (row.all { it == "O" } && row.none { it == "" }) {
                    return Player.O // O completed a line, so O loses
                }
            }
            
            // Check columns
            for (col in 0..2) {
                if (board[0][col] == "X" && board[1][col] == "X" && board[2][col] == "X") {
                    return Player.X // X completed a line, so X loses
                }
                if (board[0][col] == "O" && board[1][col] == "O" && board[2][col] == "O") {
                    return Player.O // O completed a line, so O loses
                }
            }
            
            // Check diagonals
            if (board[0][0] == "X" && board[1][1] == "X" && board[2][2] == "X") {
                return Player.X // X completed a line, so X loses
            }
            if (board[0][0] == "O" && board[1][1] == "O" && board[2][2] == "O") {
                return Player.O // O completed a line, so O loses
            }
            if (board[0][2] == "X" && board[1][1] == "X" && board[2][0] == "X") {
                return Player.X // X completed a line, so X loses
            }
            if (board[0][2] == "O" && board[1][1] == "O" && board[2][0] == "O") {
                return Player.O // O completed a line, so O loses
            }
            
            return Player.NONE // No one completed a line
        }

        // This function checks if the board is full
        fun isBoardFull(board: List<List<String>>): Boolean {
            return board.all { row -> row.all { it != "" } }
        }

        // This function checks all available moves left
        fun getAvailableMoves(board: List<List<String>>): List<Pair<Int, Int>> {
            val moves = mutableListOf<Pair<Int, Int>>()
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == "") {
                        moves.add(Pair(i, j))
                    }
                }
            }
            return moves
        }
        
        fun isGameOver(board: List<List<String>>): Boolean {
            return checkWinner(board) != Player.NONE || isBoardFull(board)
        }
    }
}
