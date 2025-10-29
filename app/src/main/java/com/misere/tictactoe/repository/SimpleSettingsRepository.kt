package com.misere.tictactoe.repository

import android.content.Context
import android.content.SharedPreferences
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode

// Simple Singleton Repository - No Hilt
class SimpleSettingsRepository private constructor(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)
    
    // Get difficulty from preferences
    fun getDifficulty(): Difficulty {
        val name = prefs.getString(KEY_DIFFICULTY, Difficulty.EASY.name) ?: Difficulty.EASY.name
        return try {
            Difficulty.valueOf(name)
        } catch (e: IllegalArgumentException) {
            Difficulty.EASY
        }
    }
    
    // Save difficulty to preferences
    fun saveDifficulty(difficulty: Difficulty) {
        prefs.edit().putString(KEY_DIFFICULTY, difficulty.name).apply()
    }
    
    // Get game mode from preferences
    fun getGameMode(): GameMode {
        val name = prefs.getString(KEY_GAME_MODE, GameMode.PLAYER_VS_BOT.name) ?: GameMode.PLAYER_VS_BOT.name
        return try {
            GameMode.valueOf(name)
        } catch (e: IllegalArgumentException) {
            GameMode.PLAYER_VS_BOT
        }
    }
    
    // Save game mode to preferences
    fun saveGameMode(gameMode: GameMode) {
        prefs.edit().putString(KEY_GAME_MODE, gameMode.name).apply()
    }
    
    companion object {
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_GAME_MODE = "game_mode"
        
        @Volatile
        private var INSTANCE: SimpleSettingsRepository? = null
        
        fun getInstance(context: Context): SimpleSettingsRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = SimpleSettingsRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

