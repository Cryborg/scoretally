package com.scoretally.domain.usecase

import com.scoretally.domain.model.Match
import com.scoretally.domain.model.MatchPlayer
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.repository.MatchRepository
import javax.inject.Inject

class CreateMatchWithPlayersUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val matchPlayerRepository: MatchPlayerRepository
) {
    suspend operator fun invoke(match: Match, playerIds: List<Long>): Long {
        val matchId = matchRepository.insertMatch(match)

        val matchPlayers = playerIds.mapIndexed { index, playerId ->
            MatchPlayer(
                matchId = matchId,
                playerId = playerId,
                score = 0,
                rank = index + 1
            )
        }

        matchPlayerRepository.insertMatchPlayers(matchPlayers)

        return matchId
    }
}
