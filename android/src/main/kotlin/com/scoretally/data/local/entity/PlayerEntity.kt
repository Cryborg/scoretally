package com.scoretally.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scoretally.domain.model.Player

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatarUri: String? = null,
    val preferredColor: String = "#6750A4"
)

fun PlayerEntity.toDomain() = Player(
    id = id,
    name = name,
    avatarUri = avatarUri,
    preferredColor = preferredColor
)

fun Player.toEntity() = PlayerEntity(
    id = id,
    name = name,
    avatarUri = avatarUri,
    preferredColor = preferredColor
)
