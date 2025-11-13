package com.scoretally.domain.model

data class MatchWithDetails(
    val match: Match,
    val game: Game,
    val playerScores: List<PlayerScore>
)

data class PlayerScore(
    val matchPlayer: MatchPlayer,
    val player: Player
)
