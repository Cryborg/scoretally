package com.scoretally.domain.model

object PredefinedGames {
    val YAHTZEE = Game(
        id = 0,
        name = "Yam's",
        minPlayers = 1,
        maxPlayers = 6,
        averageDuration = 30,
        category = "Dés",
        description = "Le jeu de dés classique avec ses combinaisons légendaires",
        scoreIncrement = 1,
        allowNegativeScores = false,
        gridType = GridType.YAHTZEE,
        isPredefined = true
    )

    val TAROT = Game(
        id = 0,
        name = "Tarot",
        minPlayers = 3,
        maxPlayers = 5,
        averageDuration = 60,
        category = "Cartes",
        description = "Jeu de cartes traditionnel français",
        scoreIncrement = 1,
        allowNegativeScores = true,
        gridType = GridType.STANDARD,
        isPredefined = true,
        isComingSoon = true
    )

    val BELOTE = Game(
        id = 0,
        name = "Belote",
        minPlayers = 4,
        maxPlayers = 4,
        averageDuration = 45,
        category = "Cartes",
        description = "Jeu de cartes classique à quatre joueurs",
        scoreIncrement = 10,
        allowNegativeScores = false,
        gridType = GridType.STANDARD,
        isPredefined = true,
        isComingSoon = true
    )

    val RAMI = Game(
        id = 0,
        name = "Rami",
        minPlayers = 2,
        maxPlayers = 6,
        averageDuration = 30,
        category = "Cartes",
        description = "Jeu de combinaisons de cartes",
        scoreIncrement = 5,
        allowNegativeScores = true,
        gridType = GridType.STANDARD,
        isPredefined = true,
        isComingSoon = true
    )

    val MOLKY = Game(
        id = 0,
        name = "Mölkky",
        minPlayers = 2,
        maxPlayers = 8,
        averageDuration = 20,
        category = "Adresse",
        description = "Jeu d'adresse finlandais avec des quilles numérotées",
        scoreIncrement = 1,
        allowNegativeScores = false,
        gridType = GridType.STANDARD,
        isPredefined = true,
        isComingSoon = true
    )

    val PETANQUE = Game(
        id = 0,
        name = "Pétanque",
        minPlayers = 2,
        maxPlayers = 6,
        averageDuration = 45,
        category = "Adresse",
        description = "Jeu de boules provençal",
        scoreIncrement = 1,
        allowNegativeScores = false,
        gridType = GridType.STANDARD,
        isPredefined = true,
        isComingSoon = true
    )

    fun getAll(): List<Game> = listOf(
        YAHTZEE,
        TAROT,
        BELOTE,
        RAMI,
        MOLKY,
        PETANQUE
    )
}
