package com.scoretally.domain.usecase

import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import javax.inject.Inject

class GetPlayerByIdUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(playerId: Long): Player? {
        return playerRepository.getPlayerById(playerId)
    }
}
