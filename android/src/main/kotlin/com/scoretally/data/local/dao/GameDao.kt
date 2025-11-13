package com.scoretally.data.local.dao

import androidx.room.*
import com.scoretally.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY name ASC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): GameEntity?

    @Query("SELECT * FROM games WHERE syncId = :syncId")
    suspend fun getGameBySyncId(syncId: String): GameEntity?

    @Query("SELECT * FROM games WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchGames(query: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Long)
}
