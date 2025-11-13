package com.scoretally.domain.model

import java.util.UUID

data class Player(
    val id: Long = 0,
    val name: String,
    val avatarUri: String? = null,
    val preferredColor: String = "#6750A4",
    val syncId: String = UUID.randomUUID().toString(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
