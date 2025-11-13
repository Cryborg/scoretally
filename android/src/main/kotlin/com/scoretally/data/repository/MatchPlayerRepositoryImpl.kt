package com.scoretally.data.repository

import com.scoretally.data.local.dao.MatchPlayerDao
import com.scoretally.data.local.entity.toDomain
import com.scoretally.data.local.entity.toEntity
import com.scoretally.domain.model.MatchPlayer
import com.scoretally.domain.repository.MatchPlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MatchPlayerRepositoryImpl @Inject constructor(
    private val matchPlayerDao: MatchPlayerDao
) : MatchPlayerRepository {

    override fun getPlayersByMatch(matchId: Long): Flow<List<MatchPlayer>> =
        matchPlayerDao.getPlayersByMatch(matchId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getMatchesByPlayer(playerId: Long): Flow<List<MatchPlayer>> =
        matchPlayerDao.getMatchesByPlayer(playerId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertMatchPlayer(matchPlayer: MatchPlayer): Long =
        matchPlayerDao.insertMatchPlayer(matchPlayer.toEntity())

    override suspend fun insertMatchPlayers(matchPlayers: List<MatchPlayer>) =
        matchPlayerDao.insertMatchPlayers(matchPlayers.map { it.toEntity() })

    override suspend fun updateMatchPlayer(matchPlayer: MatchPlayer) =
        matchPlayerDao.updateMatchPlayer(matchPlayer.toEntity())

    override suspend fun deleteMatchPlayer(matchPlayer: MatchPlayer) =
        matchPlayerDao.deleteMatchPlayer(matchPlayer.toEntity())

    override suspend fun deletePlayersByMatch(matchId: Long) =
        matchPlayerDao.deletePlayersByMatch(matchId)
}
