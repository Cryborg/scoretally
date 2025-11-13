package com.scoretally.domain.repository

import com.scoretally.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getAllMatches(): Flow<List<Match>>
    suspend fun getMatchById(matchId: Long): Match?
    fun getMatchesByGame(gameId: Long): Flow<List<Match>>
    fun getMatchesByStatus(isCompleted: Boolean): Flow<List<Match>>
    suspend fun insertMatch(match: Match): Long
    suspend fun updateMatch(match: Match)
    suspend fun deleteMatch(match: Match)
    suspend fun deleteMatchById(matchId: Long)
}
