package com.scoretally.domain.repository

import com.scoretally.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getAllPlayers(): Flow<List<Player>>
    suspend fun getPlayerById(playerId: Long): Player?
    fun searchPlayers(query: String): Flow<List<Player>>
    suspend fun insertPlayer(player: Player): Long
    suspend fun updatePlayer(player: Player)
    suspend fun deletePlayer(player: Player)
    suspend fun deletePlayerById(playerId: Long)
}
