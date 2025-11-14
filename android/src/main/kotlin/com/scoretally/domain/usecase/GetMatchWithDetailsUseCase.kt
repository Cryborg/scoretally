package com.scoretally.domain.usecase

import com.scoretally.domain.model.MatchWithDetails
import com.scoretally.domain.model.PlayerScore
import com.scoretally.domain.repository.GameRepository
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.repository.MatchRepository
import com.scoretally.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetMatchWithDetailsUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val gameRepository: GameRepository,
    private val matchPlayerRepository: MatchPlayerRepository,
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(matchId: Long): Flow<MatchWithDetails?> {
        return matchPlayerRepository.getPlayersByMatch(matchId)
            .transform { matchPlayers ->
                val match = matchRepository.getMatchById(matchId)
                if (match == null) {
                    emit(null)
                    return@transform
                }

                val game = gameRepository.getGameById(match.gameId)
                if (game == null) {
                    emit(null)
                    return@transform
                }

                val playerScores = mutableListOf<PlayerScore>()
                for (matchPlayer in matchPlayers) {
                    val player = playerRepository.getPlayerById(matchPlayer.playerId)
                    if (player == null) {
                        emit(null)
                        return@transform
                    }
                    playerScores.add(PlayerScore(matchPlayer, player))
                }

                emit(MatchWithDetails(match, game, playerScores.sortedBy { it.matchPlayer.rank }))
            }
    }
}
