package com.scoretally.domain.usecase

import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPlayersUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<List<Player>> = playerRepository.getAllPlayers()
}
