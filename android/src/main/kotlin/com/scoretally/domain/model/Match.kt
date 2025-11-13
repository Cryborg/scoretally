package com.scoretally.domain.model

import java.time.LocalDateTime

data class Match(
    val id: Long = 0,
    val gameId: Long,
    val date: LocalDateTime,
    val duration: Int?,
    val notes: String = "",
    val isCompleted: Boolean = false
)
