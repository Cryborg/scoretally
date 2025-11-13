package com.scoretally.domain.usecase

import com.scoretally.domain.model.Game
import com.scoretally.domain.repository.GameRepository
import javax.inject.Inject

class InsertGameUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(game: Game): Long {
        return gameRepository.insertGame(game)
    }
}
