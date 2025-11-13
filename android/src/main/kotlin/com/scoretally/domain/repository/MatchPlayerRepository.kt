package com.scoretally.domain.repository

import com.scoretally.domain.model.MatchPlayer
import kotlinx.coroutines.flow.Flow

interface MatchPlayerRepository {
    fun getPlayersByMatch(matchId: Long): Flow<List<MatchPlayer>>
    fun getMatchesByPlayer(playerId: Long): Flow<List<MatchPlayer>>
    suspend fun insertMatchPlayer(matchPlayer: MatchPlayer): Long
    suspend fun insertMatchPlayers(matchPlayers: List<MatchPlayer>)
    suspend fun updateMatchPlayer(matchPlayer: MatchPlayer)
    suspend fun deleteMatchPlayer(matchPlayer: MatchPlayer)
    suspend fun deletePlayersByMatch(matchId: Long)
}
