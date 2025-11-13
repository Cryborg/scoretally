package com.scoretally.data.remote.dto

import com.google.firebase.firestore.DocumentSnapshot
import com.scoretally.domain.model.Match
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class MatchDto(
    val syncId: String = "",
    val gameId: String = "",
    val dateTimestamp: Long = 0,
    val duration: Int? = null,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val lastModifiedAt: Long = 0,
    val isDeleted: Boolean = false
)

fun MatchDto.toDomain(localId: Long = 0, localGameId: Long = 0) = Match(
    id = localId,
    gameId = localGameId,
    date = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimestamp), ZoneId.systemDefault()),
    duration = duration,
    notes = notes,
    isCompleted = isCompleted,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun Match.toDto(gameSyncId: String) = MatchDto(
    syncId = syncId,
    gameId = gameSyncId,
    dateTimestamp = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    duration = duration,
    notes = notes,
    isCompleted = isCompleted,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun DocumentSnapshot.toMatchDto(): MatchDto? {
    return try {
        MatchDto(
            syncId = id,
            gameId = getString("gameId") ?: "",
            dateTimestamp = getLong("dateTimestamp") ?: 0,
            duration = getLong("duration")?.toInt(),
            notes = getString("notes") ?: "",
            isCompleted = getBoolean("isCompleted") ?: false,
            lastModifiedAt = getLong("lastModifiedAt") ?: 0,
            isDeleted = getBoolean("isDeleted") ?: false
        )
    } catch (e: Exception) {
        null
    }
}
