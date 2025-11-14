package com.scoretally.domain.usecase

import com.scoretally.domain.model.PredefinedGames
import com.scoretally.domain.repository.GameRepository
import com.scoretally.util.ResourceProvider
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class InitializePredefinedGamesUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val resourceProvider: ResourceProvider
) {
    suspend operator fun invoke() {
        val existingGames = gameRepository.getAllGames().first()
        val existingPredefinedGames = existingGames.filter { it.isPredefined }
        val predefinedGames = PredefinedGames.getAll(resourceProvider)

        if (existingPredefinedGames.isEmpty()) {
            // Aucun jeu prédéfini n'existe, les insérer tous
            predefinedGames.forEach { game ->
                gameRepository.insertGame(game)
            }
        } else {
            // Des jeux prédéfinis existent, mettre à jour chacun
            predefinedGames.forEach { newGame ->
                // Matcher par syncId plutôt que par nom pour éviter les doublons lors du changement de langue
                val existingGame = existingPredefinedGames.find { it.syncId == newGame.syncId }
                if (existingGame != null) {
                    // Mettre à jour le jeu existant en conservant son ID et certaines données utilisateur
                    gameRepository.updateGame(
                        newGame.copy(
                            id = existingGame.id,
                            bggId = existingGame.bggId,
                            imageUri = existingGame.imageUri,
                            rating = existingGame.rating,
                            notes = existingGame.notes,
                            isFavorite = existingGame.isFavorite,
                            lastModifiedAt = existingGame.lastModifiedAt
                        )
                    )
                } else {
                    // Nouveau jeu prédéfini, l'insérer
                    gameRepository.insertGame(newGame)
                }
            }
        }
    }
}
