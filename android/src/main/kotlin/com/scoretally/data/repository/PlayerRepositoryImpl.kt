package com.scoretally.data.repository

import com.scoretally.data.local.dao.PlayerDao
import com.scoretally.data.local.entity.toDomain
import com.scoretally.data.local.entity.toEntity
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao
) : PlayerRepository {

    override fun getAllPlayers(): Flow<List<Player>> =
        playerDao.getAllPlayers().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getPlayerById(playerId: Long): Player? =
        playerDao.getPlayerById(playerId)?.toDomain()

    override fun searchPlayers(query: String): Flow<List<Player>> =
        playerDao.searchPlayers(query).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertPlayer(player: Player): Long =
        playerDao.insertPlayer(player.toEntity())

    override suspend fun updatePlayer(player: Player) =
        playerDao.updatePlayer(player.toEntity())

    override suspend fun deletePlayer(player: Player) =
        playerDao.deletePlayer(player.toEntity())

    override suspend fun deletePlayerById(playerId: Long) =
        playerDao.deletePlayerById(playerId)
}
