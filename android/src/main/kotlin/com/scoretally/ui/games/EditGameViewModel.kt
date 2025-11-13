package com.scoretally.ui.games

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Game
import com.scoretally.domain.model.GridType
import com.scoretally.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditGameUiState(
    val gameId: Long = 0,
    val name: String = "",
    val minPlayers: String = "",
    val maxPlayers: String = "",
    val averageDuration: String = "",
    val category: String = "",
    val description: String = "",
    val scoreIncrement: String = "1",
    val hasDice: Boolean = false,
    val diceCount: String = "1",
    val diceFaces: String = "6",
    val allowNegativeScores: Boolean = true,
    val gridType: GridType = GridType.STANDARD,
    val isPredefined: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class EditGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gameId: Long = checkNotNull(savedStateHandle["gameId"])

    private val _uiState = MutableStateFlow(EditGameUiState(gameId = gameId))
    val uiState: StateFlow<EditGameUiState> = _uiState.asStateFlow()

    init {
        loadGame()
    }

    private fun loadGame() {
        viewModelScope.launch {
            try {
                val game = gameRepository.getGameById(gameId)
                if (game != null) {
                    _uiState.value = EditGameUiState(
                        gameId = game.id,
                        name = game.name,
                        minPlayers = game.minPlayers.toString(),
                        maxPlayers = game.maxPlayers.toString(),
                        averageDuration = game.averageDuration.toString(),
                        category = game.category,
                        description = game.description,
                        scoreIncrement = game.scoreIncrement.toString(),
                        hasDice = game.hasDice,
                        diceCount = game.diceCount.toString(),
                        diceFaces = game.diceFaces.toString(),
                        allowNegativeScores = game.allowNegativeScores,
                        gridType = game.gridType,
                        isPredefined = game.isPredefined,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

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

    fun onDiceCountChange(count: String) {
        _uiState.value = _uiState.value.copy(diceCount = count)
    }

    fun onHasDiceChange(hasDice: Boolean) {
        _uiState.value = _uiState.value.copy(hasDice = hasDice)
    }

    fun onDiceFacesChange(faces: String) {
        _uiState.value = _uiState.value.copy(diceFaces = faces)
    }

    fun onAllowNegativeScoresChange(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowNegativeScores = allow)
    }

    fun updateGame() {
        val state = _uiState.value

        if (state.name.isBlank()) return

        val minPlayers = state.minPlayers.toIntOrNull() ?: 1
        val maxPlayers = state.maxPlayers.toIntOrNull() ?: minPlayers
        val duration = state.averageDuration.toIntOrNull() ?: 30
        val scoreIncrement = state.scoreIncrement.toIntOrNull() ?: 1
        val diceCount = state.diceCount.toIntOrNull() ?: 1
        val diceFaces = state.diceFaces.toIntOrNull() ?: 6

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val existingGame = gameRepository.getGameById(gameId)
                if (existingGame != null) {
                    val updatedGame = existingGame.copy(
                        name = state.name.trim(),
                        minPlayers = minPlayers,
                        maxPlayers = maxPlayers,
                        averageDuration = duration,
                        category = state.category.trim(),
                        description = state.description.trim(),
                        scoreIncrement = scoreIncrement,
                        hasDice = state.hasDice,
                        diceCount = diceCount,
                        diceFaces = diceFaces,
                        allowNegativeScores = state.allowNegativeScores,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                    gameRepository.updateGame(updatedGame)
                    _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
                } else {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }
}
