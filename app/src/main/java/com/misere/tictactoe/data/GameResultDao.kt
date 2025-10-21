package com.misere.tictactoe.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {
    @Query("SELECT * FROM game_results ORDER BY dateTime DESC")
    fun getAllGameResults(): Flow<List<GameResultEntity>>

    @Insert
    suspend fun insertGameResult(gameResult: GameResultEntity)

    @Delete
    suspend fun deleteGameResult(gameResult: GameResultEntity)

    @Query("DELETE FROM game_results")
    suspend fun deleteAllGameResults()
}

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateTime: Long,
    val winner: String,
    val difficulty: String,
    val gameMode: String,
    val isDraw: Boolean
)

fun GameResultEntity.toGameResult(): GameResult {
    return GameResult(
        id = id,
        dateTime = dateTime,
        winner = winner,
        difficulty = difficulty,
        gameMode = gameMode,
        isDraw = isDraw
    )
}

fun GameResult.toGameResultEntity(): GameResultEntity {
    return GameResultEntity(
        id = id,
        dateTime = dateTime,
        winner = winner,
        difficulty = difficulty,
        gameMode = gameMode,
        isDraw = isDraw
    )
}
