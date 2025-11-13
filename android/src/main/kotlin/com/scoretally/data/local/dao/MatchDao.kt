package com.scoretally.data.local.dao

import androidx.room.*
import com.scoretally.data.local.entity.MatchEntity
import com.scoretally.data.local.entity.MatchWithGameNameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY dateTimestamp DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("""
        SELECT m.*, g.name as gameName
        FROM matches m
        INNER JOIN games g ON m.gameId = g.id
        ORDER BY m.dateTimestamp DESC
    """)
    fun getAllMatchesWithGameName(): Flow<List<MatchWithGameNameEntity>>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Long): MatchEntity?

    @Query("SELECT * FROM matches WHERE syncId = :syncId")
    suspend fun getMatchBySyncId(syncId: String): MatchEntity?

    @Query("SELECT * FROM matches WHERE gameId = :gameId ORDER BY dateTimestamp DESC")
    fun getMatchesByGame(gameId: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE isCompleted = :isCompleted ORDER BY dateTimestamp DESC")
    fun getMatchesByStatus(isCompleted: Boolean): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE id = :matchId")
    suspend fun deleteMatchById(matchId: Long)
}
