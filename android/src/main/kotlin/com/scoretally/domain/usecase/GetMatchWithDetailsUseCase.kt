package com.scoretally.domain.usecase

import com.scoretally.domain.model.MatchWithDetails
import com.scoretally.domain.model.PlayerScore
import com.scoretally.domain.repository.GameRepository
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.repository.MatchRepository
import com.scoretally.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMatchWithDetailsUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val gameRepository: GameRepository,
    private val matchPlayerRepository: MatchPlayerRepository,
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(matchId: Long): Flow<MatchWithDetails?> {
        return matchPlayerRepository.getPlayersByMatch(matchId)
            .combine(kotlinx.coroutines.flow.flowOf(Unit)) { matchPlayers, _ ->
                val match = matchRepository.getMatchById(matchId) ?: return@combine null
                val game = gameRepository.getGameById(match.gameId) ?: return@combine null

                val playerScores = matchPlayers.map { matchPlayer ->
                    val player = playerRepository.getPlayerById(matchPlayer.playerId)
                    PlayerScore(matchPlayer, player ?: return@combine null)
                }.sortedBy { it.matchPlayer.rank }

                MatchWithDetails(match, game, playerScores)
            }
    }
}
