package com.misere.tictactoe.di

import android.content.Context
import androidx.room.Room
import com.misere.tictactoe.data.AppDatabase
import com.misere.tictactoe.data.GameResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "misere_tictactoe_database"
        ).build()
    }

    @Provides
    fun provideGameResultDao(database: AppDatabase): GameResultDao {
        return database.gameResultDao()
    }
}
