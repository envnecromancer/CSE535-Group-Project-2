package com.misere.tictactoe.repository

import android.content.Context
import com.misere.tictactoe.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Simple Singleton Repository - No Hilt
class GameRepository private constructor(context: Context) {
    
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val dao: GameResultDao = database.gameResultDao()
    
    // Get all game results as a simple list
    suspend fun getAllGameResults(): List<GameResult> = withContext(Dispatchers.IO) {
        dao.getAllGameResultsList().map { it.toGameResult() }
    }
    
    // Insert a game result
    suspend fun insertGameResult(gameResult: GameResult) = withContext(Dispatchers.IO) {
        dao.insertGameResult(gameResult.toGameResultEntity())
    }
    
    // Delete a game result
    suspend fun deleteGameResult(gameResult: GameResult) = withContext(Dispatchers.IO) {
        dao.deleteGameResult(gameResult.toGameResultEntity())
    }
    
    // Delete all game results
    suspend fun deleteAllGameResults() = withContext(Dispatchers.IO) {
        dao.deleteAllGameResults()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: GameRepository? = null
        
        fun getInstance(context: Context): GameRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = GameRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

