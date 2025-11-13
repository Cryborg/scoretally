package com.scoretally.domain.repository

import com.scoretally.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllGames(): Flow<List<Game>>
    suspend fun getGameById(gameId: Long): Game?
    fun searchGames(query: String): Flow<List<Game>>
    suspend fun insertGame(game: Game): Long
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(game: Game)
    suspend fun deleteGameById(gameId: Long)
}
