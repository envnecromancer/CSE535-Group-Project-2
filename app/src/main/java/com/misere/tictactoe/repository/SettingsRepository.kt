package com.misere.tictactoe.repository

import android.content.Context
import android.content.SharedPreferences
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)
    
    private val _difficulty = MutableStateFlow(getStoredDifficulty())
    val difficulty: Flow<Difficulty> = _difficulty.asStateFlow()
    
    private val _gameMode = MutableStateFlow(getStoredGameMode())
    val gameMode: Flow<GameMode> = _gameMode.asStateFlow()
    
    private fun getStoredDifficulty(): Difficulty {
        val difficultyName = prefs.getString(KEY_DIFFICULTY, Difficulty.EASY.name) ?: Difficulty.EASY.name
        return try {
            Difficulty.valueOf(difficultyName)
        } catch (e: IllegalArgumentException) {
            Difficulty.EASY
        }
    }
    
    private fun getStoredGameMode(): GameMode {
        val gameModeName = prefs.getString(KEY_GAME_MODE, GameMode.VS_AI.name) ?: GameMode.VS_AI.name
        return try {
            GameMode.valueOf(gameModeName)
        } catch (e: IllegalArgumentException) {
            GameMode.VS_AI
        }
    }
    
    fun saveDifficulty(difficulty: Difficulty) {
        prefs.edit().putString(KEY_DIFFICULTY, difficulty.name).apply()
        _difficulty.value = difficulty
    }
    
    fun saveGameMode(gameMode: GameMode) {
        prefs.edit().putString(KEY_GAME_MODE, gameMode.name).apply()
        _gameMode.value = gameMode
    }
    
    fun getCurrentDifficulty(): Difficulty = _difficulty.value
    fun getCurrentGameMode(): GameMode = _gameMode.value
    
    companion object {
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_GAME_MODE = "game_mode"
    }
}
