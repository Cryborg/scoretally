package com.scoretally.domain.usecase

import com.scoretally.domain.model.PredefinedGames
import com.scoretally.domain.repository.GameRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class InitializePredefinedGamesUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke() {
        // Vérifier si des jeux prédéfinis existent déjà
        val existingGames = gameRepository.getAllGames().first()
        val existingPredefinedGames = existingGames.filter { it.isPredefined }

        // Si aucun jeu prédéfini n'existe, les insérer
        if (existingPredefinedGames.isEmpty()) {
            PredefinedGames.getAll().forEach { game ->
                gameRepository.insertGame(game)
            }
        }
    }
}
