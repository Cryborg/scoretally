package com.scoretally.data.remote.dto

import com.google.firebase.firestore.DocumentSnapshot
import com.scoretally.domain.model.Game

data class GameDto(
    val syncId: String = "",
    val name: String = "",
    val minPlayers: Int = 2,
    val maxPlayers: Int = 4,
    val averageDuration: Int = 30,
    val category: String = "",
    val imageUri: String? = null,
    val description: String = "",
    val rating: Float = 0f,
    val notes: String = "",
    val scoreIncrement: Int = 1,
    val diceCount: Int = 1,
    val diceFaces: Int = 6,
    val lastModifiedAt: Long = 0,
    val isDeleted: Boolean = false
)

fun GameDto.toDomain(localId: Long = 0) = Game(
    id = localId,
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
    diceCount = diceCount,
    diceFaces = diceFaces,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun Game.toDto() = GameDto(
    syncId = syncId,
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
    diceCount = diceCount,
    diceFaces = diceFaces,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun DocumentSnapshot.toGameDto(): GameDto? {
    return try {
        GameDto(
            syncId = id,
            name = getString("name") ?: "",
            minPlayers = getLong("minPlayers")?.toInt() ?: 2,
            maxPlayers = getLong("maxPlayers")?.toInt() ?: 4,
            averageDuration = getLong("averageDuration")?.toInt() ?: 30,
            category = getString("category") ?: "",
            imageUri = getString("imageUri"),
            description = getString("description") ?: "",
            rating = getDouble("rating")?.toFloat() ?: 0f,
            notes = getString("notes") ?: "",
            scoreIncrement = getLong("scoreIncrement")?.toInt() ?: 1,
            diceCount = getLong("diceCount")?.toInt() ?: 1,
            diceFaces = getLong("diceFaces")?.toInt() ?: 6,
            lastModifiedAt = getLong("lastModifiedAt") ?: 0,
            isDeleted = getBoolean("isDeleted") ?: false
        )
    } catch (e: Exception) {
        null
    }
}
