package com.scoretally.domain.model

data class Player(
    val id: Long = 0,
    val name: String,
    val avatarUri: String? = null,
    val preferredColor: String = "#6750A4"
)
