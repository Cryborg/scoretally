package com.scoretally.di

import com.scoretally.data.repository.GameRepositoryImpl
import com.scoretally.data.repository.MatchPlayerRepositoryImpl
import com.scoretally.data.repository.MatchRepositoryImpl
import com.scoretally.data.repository.PlayerRepositoryImpl
import com.scoretally.domain.repository.GameRepository
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.repository.MatchRepository
import com.scoretally.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        matchRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository

    @Binds
    @Singleton
    abstract fun bindMatchPlayerRepository(
        matchPlayerRepositoryImpl: MatchPlayerRepositoryImpl
    ): MatchPlayerRepository
}
