package com.scoretally.data.remote.bgg.mapper

import com.scoretally.data.remote.bgg.dto.BggDetailsItem
import com.scoretally.domain.model.Game
import com.scoretally.domain.model.GridType

object BggMapper {
    fun mapToGame(bggItem: BggDetailsItem, localImageUri: String?): Game {
        val primaryName = bggItem.names
            ?.firstOrNull { it.type == "primary" }
            ?.value
            ?: bggItem.names?.firstOrNull()?.value
            ?: ""

        val category = bggItem.links
            ?.filter { it.type == "boardgamecategory" }
            ?.firstOrNull()
            ?.value
            ?: ""

        val description = bggItem.description?.let { cleanDescription(it) } ?: ""

        val minPlayers = bggItem.minPlayers?.value?.toIntOrNull() ?: 1
        val maxPlayers = bggItem.maxPlayers?.value?.toIntOrNull() ?: minPlayers
        val averageDuration = bggItem.playingTime?.value?.toIntOrNull() ?: 30
        val rating = bggItem.statistics?.ratings?.average?.value?.toFloatOrNull() ?: 0f

        return Game(
            id = 0,
            name = primaryName,
            minPlayers = minPlayers,
            maxPlayers = maxPlayers,
            averageDuration = averageDuration,
            category = category,
            imageUri = localImageUri,
            description = description,
            rating = rating,
            notes = "",
            scoreIncrement = 1,
            allowNegativeScores = true,
            gridType = GridType.STANDARD,
            hasDice = false,
            diceCount = 1,
            diceFaces = 6,
            isPredefined = false,
            isComingSoon = false,
            bggId = bggItem.id
        )
    }

    private fun cleanDescription(html: String): String {
        return html
            .replace("&#10;", "\n")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace(Regex("<[^>]*>"), "")
            .trim()
    }
}
