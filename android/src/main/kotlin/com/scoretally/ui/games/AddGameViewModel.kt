package com.scoretally.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.data.remote.bgg.dto.BggSearchItem
import com.scoretally.domain.repository.BggRepository
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
    val hasDice: Boolean = false,
    val diceCount: String = "1",
    val diceFaces: String = "6",
    val allowNegativeScores: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val bggSearchResults: List<BggSearchItem>? = null,
    val isSearchingBgg: Boolean = false,
    val bggSearchError: String? = null
)

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val bggRepository: BggRepository
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

    fun saveGame() {
        val state = _uiState.value

        if (!GameFormParser.isFormValid(state.name)) return

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val game = GameFormParser.buildGame(
                    name = state.name,
                    minPlayers = state.minPlayers,
                    maxPlayers = state.maxPlayers,
                    averageDuration = state.averageDuration,
                    category = state.category,
                    description = state.description,
                    scoreIncrement = state.scoreIncrement,
                    hasDice = state.hasDice,
                    diceCount = state.diceCount,
                    diceFaces = state.diceFaces,
                    allowNegativeScores = state.allowNegativeScores
                )
                gameRepository.insertGame(game)
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }

    fun searchBgg(query: String) {
        if (query.isBlank()) return

        _uiState.value = _uiState.value.copy(isSearchingBgg = true, bggSearchError = null)

        viewModelScope.launch {
            val result = bggRepository.searchGames(query)
            result.fold(
                onSuccess = { items ->
                    _uiState.value = _uiState.value.copy(
                        bggSearchResults = items,
                        isSearchingBgg = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSearchingBgg = false,
                        bggSearchError = error.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun selectBggGame(bggId: String) {
        _uiState.value = _uiState.value.copy(isSearchingBgg = true)

        viewModelScope.launch {
            val result = bggRepository.getGameDetails(bggId)
            result.fold(
                onSuccess = { game ->
                    _uiState.value = _uiState.value.copy(
                        name = game.name,
                        minPlayers = game.minPlayers.toString(),
                        maxPlayers = game.maxPlayers.toString(),
                        averageDuration = game.averageDuration.toString(),
                        category = game.category,
                        description = game.description,
                        bggSearchResults = null,
                        isSearchingBgg = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSearchingBgg = false,
                        bggSearchError = error.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun dismissBggSearch() {
        _uiState.value = _uiState.value.copy(
            bggSearchResults = null,
            bggSearchError = null
        )
    }
}
