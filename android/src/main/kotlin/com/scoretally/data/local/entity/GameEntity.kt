package com.scoretally.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scoretally.domain.model.Game
import com.scoretally.domain.model.GridType
import java.util.UUID

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val averageDuration: Int,
    val category: String = "",
    val imageUri: String? = null,
    val description: String = "",
    val rating: Float = 0f,
    val notes: String = "",
    val scoreIncrement: Int = 1,
    val allowNegativeScores: Boolean = true,
    val gridType: String = GridType.STANDARD.name,
    val hasDice: Boolean = false,
    val diceCount: Int = 1,
    val diceFaces: Int = 6,
    val isPredefined: Boolean = false,
    val isComingSoon: Boolean = false,
    val isFavorite: Boolean = false,
    val bggId: String? = null,
    val syncId: String = UUID.randomUUID().toString(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

fun GameEntity.toDomain() = Game(
    id = id,
    name = name,
    minPlayers = minPlayers,
    maxPlayers = maxPlayers,
    averageDuration = averageDuration,
    category = category,
    imageUri = imageUri,
    description = description,
    rating = rating,
    notes = notes,
    scoreIncrement = scoreIncrement,
    allowNegativeScores = allowNegativeScores,
    gridType = GridType.fromString(gridType),
    hasDice = hasDice,
    diceCount = diceCount,
    diceFaces = diceFaces,
    isPredefined = isPredefined,
    isComingSoon = isComingSoon,
    isFavorite = isFavorite,
    bggId = bggId,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun Game.toEntity() = GameEntity(
    id = id,
    name = name,
    minPlayers = minPlayers,
    maxPlayers = maxPlayers,
    averageDuration = averageDuration,
    category = category,
    imageUri = imageUri,
    description = description,
    rating = rating,
    notes = notes,
    scoreIncrement = scoreIncrement,
    allowNegativeScores = allowNegativeScores,
    gridType = gridType.name,
    hasDice = hasDice,
    diceCount = diceCount,
    diceFaces = diceFaces,
    isPredefined = isPredefined,
    isComingSoon = isComingSoon,
    isFavorite = isFavorite,
    bggId = bggId,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)
