package com.scoretally.data.local.dao

import androidx.room.*
import com.scoretally.data.local.entity.MatchPlayerEntity
import com.scoretally.data.local.entity.MatchPlayerWithName
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchPlayerDao {
    @Query("SELECT * FROM match_players WHERE matchId = :matchId ORDER BY rank ASC")
    fun getPlayersByMatch(matchId: Long): Flow<List<MatchPlayerEntity>>

    @Query("""
        SELECT mp.matchId, p.name as playerName
        FROM match_players mp
        INNER JOIN players p ON mp.playerId = p.id
        ORDER BY mp.matchId, mp.rank ASC
    """)
    fun getAllMatchPlayersWithNames(): Flow<List<MatchPlayerWithName>>

    @Query("SELECT * FROM match_players WHERE playerId = :playerId ORDER BY matchId DESC")
    fun getMatchesByPlayer(playerId: Long): Flow<List<MatchPlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchPlayer(matchPlayer: MatchPlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchPlayers(matchPlayers: List<MatchPlayerEntity>)

    @Update
    suspend fun updateMatchPlayer(matchPlayer: MatchPlayerEntity)

    @Delete
    suspend fun deleteMatchPlayer(matchPlayer: MatchPlayerEntity)

    @Query("DELETE FROM match_players WHERE matchId = :matchId")
    suspend fun deletePlayersByMatch(matchId: Long)
}
