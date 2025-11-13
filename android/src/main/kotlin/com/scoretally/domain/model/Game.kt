package com.scoretally.domain.model

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
    val scoreIncrement: Int = 1
)
