package com.scoretally.domain.usecase

import com.scoretally.domain.model.MatchPlayer
import com.scoretally.domain.repository.MatchPlayerRepository
import javax.inject.Inject

class UpdatePlayerScoreUseCase @Inject constructor(
    private val matchPlayerRepository: MatchPlayerRepository
) {
    suspend operator fun invoke(matchPlayer: MatchPlayer, newScore: Int) {
        matchPlayerRepository.updateMatchPlayer(
            matchPlayer.copy(score = newScore)
        )
    }
}
