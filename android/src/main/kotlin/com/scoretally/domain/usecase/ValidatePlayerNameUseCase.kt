package com.scoretally.domain.usecase

import com.scoretally.R
import com.scoretally.domain.repository.PlayerRepository
import com.scoretally.util.ResourceProvider
import kotlinx.coroutines.flow.first
import javax.inject.Inject

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}

/**
 * Use case to validate player name uniqueness
 * Checks if a player name already exists in the database
 */
class ValidatePlayerNameUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val resourceProvider: ResourceProvider
) {
    /**
     * Validates that a player name is unique
     * @param name The player name to validate
     * @param excludePlayerId Player ID to exclude from validation (for edit scenarios)
     * @return ValidationResult indicating if the name is valid or not
     */
    suspend operator fun invoke(name: String, excludePlayerId: Long? = null): ValidationResult {
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            return ValidationResult.Valid // Let the UI handle blank validation
        }

        val existingPlayers = playerRepository.getAllPlayers().first()
        val nameExists = existingPlayers.any { player ->
            val matchesName = player.name.equals(trimmedName, ignoreCase = true)
            val isNotExcluded = excludePlayerId == null || player.id != excludePlayerId
            matchesName && isNotExcluded
        }

        return if (nameExists) {
            ValidationResult.Invalid(resourceProvider.getString(R.string.error_player_name_exists))
        } else {
            ValidationResult.Valid
        }
    }
}
