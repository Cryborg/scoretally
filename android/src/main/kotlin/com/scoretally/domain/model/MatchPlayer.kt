package com.scoretally.domain.model

data class MatchPlayer(
    val id: Long = 0,
    val matchId: Long,
    val playerId: Long,
    val score: Int,
    val rank: Int
)
