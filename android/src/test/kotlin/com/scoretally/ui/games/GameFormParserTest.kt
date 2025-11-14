package com.scoretally.ui.games

import com.google.common.truth.Truth.assertThat
import com.scoretally.domain.model.GridType
import org.junit.Test

class GameFormParserTest {

    @Test
    fun `parseMinPlayers with valid number returns the number`() {
        val result = GameFormParser.parseMinPlayers("5")
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun `parseMinPlayers with invalid input returns default 1`() {
        val result = GameFormParser.parseMinPlayers("invalid")
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `parseMinPlayers with empty string returns default 1`() {
        val result = GameFormParser.parseMinPlayers("")
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `parseMaxPlayers with valid number returns the number`() {
        val result = GameFormParser.parseMaxPlayers("10", 2)
        assertThat(result).isEqualTo(10)
    }

    @Test
    fun `parseMaxPlayers with invalid input returns minPlayers`() {
        val result = GameFormParser.parseMaxPlayers("invalid", 3)
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `parseAverageDuration with valid number returns the number`() {
        val result = GameFormParser.parseAverageDuration("60")
        assertThat(result).isEqualTo(60)
    }

    @Test
    fun `parseAverageDuration with invalid input returns default 30`() {
        val result = GameFormParser.parseAverageDuration("invalid")
        assertThat(result).isEqualTo(30)
    }

    @Test
    fun `parseScoreIncrement with valid number returns the number`() {
        val result = GameFormParser.parseScoreIncrement("5")
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun `parseScoreIncrement with invalid input returns default 1`() {
        val result = GameFormParser.parseScoreIncrement("invalid")
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `parseDiceCount with valid number returns the number`() {
        val result = GameFormParser.parseDiceCount("3")
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `parseDiceCount with invalid input returns default 1`() {
        val result = GameFormParser.parseDiceCount("invalid")
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `parseDiceFaces with valid number returns the number`() {
        val result = GameFormParser.parseDiceFaces("20")
        assertThat(result).isEqualTo(20)
    }

    @Test
    fun `parseDiceFaces with invalid input returns default 6`() {
        val result = GameFormParser.parseDiceFaces("invalid")
        assertThat(result).isEqualTo(6)
    }

    @Test
    fun `isFormValid returns true when name is not blank`() {
        val result = GameFormParser.isFormValid("Chess")
        assertThat(result).isTrue()
    }

    @Test
    fun `isFormValid returns false when name is blank`() {
        val result = GameFormParser.isFormValid("")
        assertThat(result).isFalse()
    }

    @Test
    fun `isFormValid returns false when name is whitespace only`() {
        val result = GameFormParser.isFormValid("   ")
        assertThat(result).isFalse()
    }

    @Test
    fun `buildGame creates game with all fields correctly`() {
        val game = GameFormParser.buildGame(
            name = "  Chess  ",
            minPlayers = "2",
            maxPlayers = "4",
            averageDuration = "60",
            category = "  Strategy  ",
            description = "  A classic board game  ",
            scoreIncrement = "5",
            hasDice = false,
            diceCount = "1",
            diceFaces = "6",
            allowNegativeScores = false,
            gridType = GridType.STANDARD
        )

        assertThat(game.name).isEqualTo("Chess") // trimmed
        assertThat(game.minPlayers).isEqualTo(2)
        assertThat(game.maxPlayers).isEqualTo(4)
        assertThat(game.averageDuration).isEqualTo(60)
        assertThat(game.category).isEqualTo("Strategy") // trimmed
        assertThat(game.description).isEqualTo("A classic board game") // trimmed
        assertThat(game.scoreIncrement).isEqualTo(5)
        assertThat(game.hasDice).isFalse()
        assertThat(game.diceCount).isEqualTo(1)
        assertThat(game.diceFaces).isEqualTo(6)
        assertThat(game.allowNegativeScores).isFalse()
        assertThat(game.gridType).isEqualTo(GridType.STANDARD)
    }

    @Test
    fun `buildGame uses defaults for invalid inputs`() {
        val game = GameFormParser.buildGame(
            name = "Poker",
            minPlayers = "invalid",
            maxPlayers = "invalid",
            averageDuration = "invalid",
            category = "",
            description = "",
            scoreIncrement = "invalid",
            hasDice = true,
            diceCount = "invalid",
            diceFaces = "invalid",
            allowNegativeScores = true
        )

        assertThat(game.name).isEqualTo("Poker")
        assertThat(game.minPlayers).isEqualTo(1) // default
        assertThat(game.maxPlayers).isEqualTo(1) // default (same as minPlayers)
        assertThat(game.averageDuration).isEqualTo(30) // default
        assertThat(game.scoreIncrement).isEqualTo(1) // default
        assertThat(game.diceCount).isEqualTo(1) // default
        assertThat(game.diceFaces).isEqualTo(6) // default
    }

    @Test
    fun `buildGame defaults maxPlayers to minPlayers when maxPlayers is invalid`() {
        val game = GameFormParser.buildGame(
            name = "Test",
            minPlayers = "5",
            maxPlayers = "invalid",
            averageDuration = "30",
            category = "",
            description = "",
            scoreIncrement = "1",
            hasDice = false,
            diceCount = "1",
            diceFaces = "6",
            allowNegativeScores = false
        )

        assertThat(game.minPlayers).isEqualTo(5)
        assertThat(game.maxPlayers).isEqualTo(5) // defaults to minPlayers
    }
}
