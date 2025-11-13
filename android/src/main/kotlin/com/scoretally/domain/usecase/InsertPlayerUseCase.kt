package com.scoretally.domain.usecase

import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import javax.inject.Inject

class InsertPlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(player: Player): Long {
        return playerRepository.insertPlayer(player)
    }
}
