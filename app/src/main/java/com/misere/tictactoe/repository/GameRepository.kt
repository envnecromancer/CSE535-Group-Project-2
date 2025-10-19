package com.misere.tictactoe.repository

import com.misere.tictactoe.data.GameResult
import com.misere.tictactoe.data.GameResultDao
import com.misere.tictactoe.data.GameResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameResultDao: GameResultDao
) {
    fun getAllGameResults(): Flow<List<GameResult>> {
        return gameResultDao.getAllGameResults()
            .map { entities -> entities.map { it.toGameResult() } }
    }
    
    suspend fun insertGameResult(gameResult: GameResult) {
        gameResultDao.insertGameResult(gameResult.toGameResultEntity())
    }
    
    suspend fun deleteGameResult(gameResult: GameResult) {
        gameResultDao.deleteGameResult(gameResult.toGameResultEntity())
    }
    
    suspend fun deleteAllGameResults() {
        gameResultDao.deleteAllGameResults()
    }
}
