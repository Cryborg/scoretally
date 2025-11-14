package com.scoretally.domain.model

import java.util.UUID

data class Game(
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
    val gridType: GridType = GridType.STANDARD,
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
