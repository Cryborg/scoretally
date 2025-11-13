package com.scoretally.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scoretally.domain.model.MatchPlayer

@Entity(
    tableName = "match_players",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("matchId"),
        Index("playerId")
    ]
)
data class MatchPlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val playerId: Long,
    val score: Int,
    val rank: Int
)

fun MatchPlayerEntity.toDomain() = MatchPlayer(
    id = id,
    matchId = matchId,
    playerId = playerId,
    score = score,
    rank = rank
)

fun MatchPlayer.toEntity() = MatchPlayerEntity(
    id = id,
    matchId = matchId,
    playerId = playerId,
    score = score,
    rank = rank
)
