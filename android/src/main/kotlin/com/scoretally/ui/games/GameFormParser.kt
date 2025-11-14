package com.scoretally.ui.games

import com.scoretally.domain.model.Game
import com.scoretally.domain.model.GridType

object GameFormParser {

    fun parseMinPlayers(value: String): Int = value.toIntOrNull() ?: 1

    fun parseMaxPlayers(value: String, minPlayers: Int): Int {
        return value.toIntOrNull() ?: minPlayers
    }

    fun parseAverageDuration(value: String): Int = value.toIntOrNull() ?: 30

    fun parseScoreIncrement(value: String): Int = value.toIntOrNull() ?: 1

    fun parseDiceCount(value: String): Int = value.toIntOrNull() ?: 1

    fun parseDiceFaces(value: String): Int = value.toIntOrNull() ?: 6

    fun isFormValid(name: String): Boolean = name.isNotBlank()

    fun buildGame(
        name: String,
        minPlayers: String,
        maxPlayers: String,
        averageDuration: String,
        category: String,
        description: String,
        scoreIncrement: String,
        hasDice: Boolean,
        diceCount: String,
        diceFaces: String,
        allowNegativeScores: Boolean,
        gridType: GridType = GridType.STANDARD
    ): Game {
        val parsedMinPlayers = parseMinPlayers(minPlayers)
        val parsedMaxPlayers = parseMaxPlayers(maxPlayers, parsedMinPlayers)

        return Game(
            name = name.trim(),
            minPlayers = parsedMinPlayers,
            maxPlayers = parsedMaxPlayers,
            averageDuration = parseAverageDuration(averageDuration),
            category = category.trim(),
            description = description.trim(),
            scoreIncrement = parseScoreIncrement(scoreIncrement),
            hasDice = hasDice,
            diceCount = parseDiceCount(diceCount),
            diceFaces = parseDiceFaces(diceFaces),
            allowNegativeScores = allowNegativeScores,
            gridType = gridType
        )
    }
}
