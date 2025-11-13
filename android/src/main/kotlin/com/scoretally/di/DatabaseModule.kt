package com.scoretally.di

import android.content.Context
import androidx.room.Room
import com.scoretally.data.local.dao.GameDao
import com.scoretally.data.local.dao.MatchDao
import com.scoretally.data.local.dao.MatchPlayerDao
import com.scoretally.data.local.dao.PlayerDao
import com.scoretally.data.local.database.ScoreTallyDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): ScoreTallyDatabase {
        return Room.databaseBuilder(
            context,
            ScoreTallyDatabase::class.java,
            "scoretally_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideGameDao(database: ScoreTallyDatabase): GameDao =
        database.gameDao()

    @Provides
    fun providePlayerDao(database: ScoreTallyDatabase): PlayerDao =
        database.playerDao()

    @Provides
    fun provideMatchDao(database: ScoreTallyDatabase): MatchDao =
        database.matchDao()

    @Provides
    fun provideMatchPlayerDao(database: ScoreTallyDatabase): MatchPlayerDao =
        database.matchPlayerDao()
}
