package com.scoretally.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scoretally.domain.model.Game

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
    val scoreIncrement: Int = 1
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
    scoreIncrement = scoreIncrement
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
    scoreIncrement = scoreIncrement
)
