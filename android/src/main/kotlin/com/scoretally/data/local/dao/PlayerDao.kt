package com.scoretally.data.local.dao

import androidx.room.*
import com.scoretally.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE syncId = :syncId")
    suspend fun getPlayerBySyncId(syncId: String): PlayerEntity?

    @Query("SELECT * FROM players WHERE name LIKE '%' || :query || '%'")
    fun searchPlayers(query: String): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deletePlayerById(playerId: Long)
}
