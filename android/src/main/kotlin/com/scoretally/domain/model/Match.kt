package com.scoretally.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Match(
    val id: Long = 0,
    val gameId: Long,
    val date: LocalDateTime,
    val duration: Int?,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val syncId: String = UUID.randomUUID().toString(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
