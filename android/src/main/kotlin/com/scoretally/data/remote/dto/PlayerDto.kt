package com.scoretally.data.remote.dto

import com.google.firebase.firestore.DocumentSnapshot
import com.scoretally.domain.model.Player

data class PlayerDto(
    val syncId: String = "",
    val name: String = "",
    val avatarUri: String? = null,
    val preferredColor: String = "#6750A4",
    val lastModifiedAt: Long = 0,
    val isDeleted: Boolean = false
)

fun PlayerDto.toDomain(localId: Long = 0) = Player(
    id = localId,
    name = name,
    avatarUri = avatarUri,
    preferredColor = preferredColor,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun Player.toDto() = PlayerDto(
    syncId = syncId,
    name = name,
    avatarUri = avatarUri,
    preferredColor = preferredColor,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun DocumentSnapshot.toPlayerDto(): PlayerDto? {
    return try {
        PlayerDto(
            syncId = id,
            name = getString("name") ?: "",
            avatarUri = getString("avatarUri"),
            preferredColor = getString("preferredColor") ?: "#6750A4",
            lastModifiedAt = getLong("lastModifiedAt") ?: 0,
            isDeleted = getBoolean("isDeleted") ?: false
        )
    } catch (e: Exception) {
        null
    }
}
