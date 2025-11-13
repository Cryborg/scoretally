package com.scoretally.data.repository

import com.scoretally.data.local.dao.GameDao
import com.scoretally.data.local.entity.toDomain
import com.scoretally.data.local.entity.toEntity
import com.scoretally.domain.model.Game
import com.scoretally.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao
) : GameRepository {

    override fun getAllGames(): Flow<List<Game>> =
        gameDao.getAllGames().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getGameById(gameId: Long): Game? =
        gameDao.getGameById(gameId)?.toDomain()

    override fun searchGames(query: String): Flow<List<Game>> =
        gameDao.searchGames(query).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertGame(game: Game): Long =
        gameDao.insertGame(game.toEntity())

    override suspend fun updateGame(game: Game) =
        gameDao.updateGame(game.toEntity())

    override suspend fun deleteGame(game: Game) =
        gameDao.deleteGame(game.toEntity())

    override suspend fun deleteGameById(gameId: Long) =
        gameDao.deleteGameById(gameId)
}
