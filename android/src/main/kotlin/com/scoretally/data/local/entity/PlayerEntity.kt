package com.scoretally.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scoretally.domain.model.Player
import java.util.UUID

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatarUri: String? = null,
    val preferredColor: String = "#6750A4",
    val syncId: String = UUID.randomUUID().toString(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

fun PlayerEntity.toDomain() = Player(
    id = id,
    name = name,
    avatarUri = avatarUri,
    preferredColor = preferredColor,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)

fun Player.toEntity() = PlayerEntity(
    id = id,
    name = name,
    avatarUri = avatarUri,
    preferredColor = preferredColor,
    syncId = syncId,
    lastModifiedAt = lastModifiedAt,
    isDeleted = isDeleted
)
