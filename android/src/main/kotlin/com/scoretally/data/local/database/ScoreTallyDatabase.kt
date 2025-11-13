package com.scoretally.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scoretally.data.local.dao.GameDao
import com.scoretally.data.local.dao.MatchDao
import com.scoretally.data.local.dao.MatchPlayerDao
import com.scoretally.data.local.dao.PlayerDao
import com.scoretally.data.local.entity.GameEntity
import com.scoretally.data.local.entity.MatchEntity
import com.scoretally.data.local.entity.MatchPlayerEntity
import com.scoretally.data.local.entity.PlayerEntity

@Database(
    entities = [
        GameEntity::class,
        PlayerEntity::class,
        MatchEntity::class,
        MatchPlayerEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class ScoreTallyDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun matchPlayerDao(): MatchPlayerDao
}
