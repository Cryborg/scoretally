package com.scoretally.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Game
import com.scoretally.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddGameUiState(
    val name: String = "",
    val minPlayers: String = "",
    val maxPlayers: String = "",
    val averageDuration: String = "",
    val category: String = "",
    val description: String = "",
    val scoreIncrement: String = "1",
    val allowNegativeScores: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGameUiState())
    val uiState: StateFlow<AddGameUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onMinPlayersChange(minPlayers: String) {
        _uiState.value = _uiState.value.copy(minPlayers = minPlayers)
    }

    fun onMaxPlayersChange(maxPlayers: String) {
        _uiState.value = _uiState.value.copy(maxPlayers = maxPlayers)
    }

    fun onAverageDurationChange(duration: String) {
        _uiState.value = _uiState.value.copy(averageDuration = duration)
    }

    fun onCategoryChange(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onScoreIncrementChange(increment: String) {
        _uiState.value = _uiState.value.copy(scoreIncrement = increment)
    }

    fun onAllowNegativeScoresChange(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowNegativeScores = allow)
    }

    fun saveGame() {
        val state = _uiState.value

        if (state.name.isBlank()) return

        val minPlayers = state.minPlayers.toIntOrNull() ?: 1
        val maxPlayers = state.maxPlayers.toIntOrNull() ?: minPlayers
        val duration = state.averageDuration.toIntOrNull() ?: 30
        val scoreIncrement = state.scoreIncrement.toIntOrNull() ?: 1

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val game = Game(
                    name = state.name,
                    minPlayers = minPlayers,
                    maxPlayers = maxPlayers,
                    averageDuration = duration,
                    category = state.category,
                    description = state.description,
                    scoreIncrement = scoreIncrement,
                    allowNegativeScores = state.allowNegativeScores
                )
                gameRepository.insertGame(game)
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }
}
