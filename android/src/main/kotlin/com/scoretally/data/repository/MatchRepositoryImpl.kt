package com.scoretally.data.repository

import com.scoretally.data.local.dao.MatchDao
import com.scoretally.data.local.entity.toDomain
import com.scoretally.data.local.entity.toEntity
import com.scoretally.domain.model.Match
import com.scoretally.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val matchDao: MatchDao
) : MatchRepository {

    override fun getAllMatches(): Flow<List<Match>> =
        matchDao.getAllMatches().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getMatchById(matchId: Long): Match? =
        matchDao.getMatchById(matchId)?.toDomain()

    override fun getMatchesByGame(gameId: Long): Flow<List<Match>> =
        matchDao.getMatchesByGame(gameId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getMatchesByStatus(isCompleted: Boolean): Flow<List<Match>> =
        matchDao.getMatchesByStatus(isCompleted).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertMatch(match: Match): Long =
        matchDao.insertMatch(match.toEntity())

    override suspend fun updateMatch(match: Match) =
        matchDao.updateMatch(match.toEntity())

    override suspend fun deleteMatch(match: Match) =
        matchDao.deleteMatch(match.toEntity())

    override suspend fun deleteMatchById(matchId: Long) =
        matchDao.deleteMatchById(matchId)
}
