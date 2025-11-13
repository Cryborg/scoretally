package com.scoretally.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scoretally.domain.model.Match
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gameId")]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val dateTimestamp: Long,
    val duration: Int? = null,
    val notes: String = "",
    val isCompleted: Boolean = false
)

fun MatchEntity.toDomain() = Match(
    id = id,
    gameId = gameId,
    date = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimestamp), ZoneId.systemDefault()),
    duration = duration,
    notes = notes,
    isCompleted = isCompleted
)

fun Match.toEntity() = MatchEntity(
    id = id,
    gameId = gameId,
    dateTimestamp = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    duration = duration,
    notes = notes,
    isCompleted = isCompleted
)
