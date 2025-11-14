package com.scoretally.domain.model

import com.scoretally.R
import com.scoretally.util.ResourceProvider

object PredefinedGames {
    // SyncIds fixes pour les jeux prédéfinis
    private const val YAHTZEE_SYNC_ID = "predefined-yahtzee"
    private const val TAROT_SYNC_ID = "predefined-tarot"
    private const val RUMMY_SYNC_ID = "predefined-rummy"

    fun getYahtzee(resourceProvider: ResourceProvider) = Game(
        id = 0,
        name = resourceProvider.getString(R.string.predefined_yahtzee_name),
        minPlayers = 1,
        maxPlayers = 6,
        averageDuration = 30,
        category = resourceProvider.getString(R.string.predefined_yahtzee_category),
        description = resourceProvider.getString(R.string.predefined_yahtzee_description),
        scoreIncrement = 1,
        allowNegativeScores = false,
        gridType = GridType.YAHTZEE,
        isPredefined = true,
        syncId = YAHTZEE_SYNC_ID
    )

    fun getTarot(resourceProvider: ResourceProvider) = Game(
        id = 0,
        name = resourceProvider.getString(R.string.predefined_tarot_name),
        minPlayers = 3,
        maxPlayers = 5,
        averageDuration = 60,
        category = resourceProvider.getString(R.string.predefined_tarot_category),
        description = resourceProvider.getString(R.string.predefined_tarot_description),
        scoreIncrement = 1,
        allowNegativeScores = true,
        gridType = GridType.TAROT,
        isPredefined = true,
        syncId = TAROT_SYNC_ID
    )

    fun getRummy(resourceProvider: ResourceProvider) = Game(
        id = 0,
        name = resourceProvider.getString(R.string.predefined_rummy_name),
        minPlayers = 2,
        maxPlayers = 6,
        averageDuration = 30,
        category = resourceProvider.getString(R.string.predefined_rummy_category),
        description = resourceProvider.getString(R.string.predefined_rummy_description),
        scoreIncrement = 5,
        allowNegativeScores = true,
        gridType = GridType.RUMMY,
        isPredefined = true,
        syncId = RUMMY_SYNC_ID
    )

    fun getAll(resourceProvider: ResourceProvider): List<Game> = listOf(
        getYahtzee(resourceProvider),
        getTarot(resourceProvider),
        getRummy(resourceProvider)
    )
}
