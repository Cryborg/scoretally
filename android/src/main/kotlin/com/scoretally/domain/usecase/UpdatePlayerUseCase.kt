package com.scoretally.domain.usecase

import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import javax.inject.Inject

class UpdatePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(player: Player) {
        playerRepository.updatePlayer(player)
    }
}
