package com.scoretally.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.data.remote.bgg.dto.BggSearchItem
import com.scoretally.domain.model.Game
import com.scoretally.domain.repository.BggRepository
import com.scoretally.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchBggUiState(
    val query: String = "",
    val searchResults: List<BggSearchItem> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,
    val selectedGame: Game? = null,
    val isLoadingDetails: Boolean = false,
    val detailsError: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class SearchBggViewModel @Inject constructor(
    private val bggRepository: BggRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchBggUiState())
    val uiState: StateFlow<SearchBggUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun searchGames() {
        val query = _uiState.value.query.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSearching = true,
                searchError = null,
                searchResults = emptyList()
            )

            bggRepository.searchGames(query)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results,
                        isSearching = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        searchError = error.message ?: "Erreur de recherche",
                        isSearching = false
                    )
                }
        }
    }

    fun loadAndSaveGameDetails(bggId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingDetails = true,
                detailsError = null,
                selectedGame = null
            )

            bggRepository.getGameDetails(bggId)
                .onSuccess { game ->
                    gameRepository.insertGame(game)
                    _uiState.value = _uiState.value.copy(
                        selectedGame = game,
                        isLoadingDetails = false,
                        isSaved = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        detailsError = error.message ?: "Erreur de chargement",
                        isLoadingDetails = false
                    )
                }
        }
    }

    fun clearSelectedGame() {
        _uiState.value = _uiState.value.copy(selectedGame = null)
    }

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            query = "",
            searchResults = emptyList(),
            searchError = null
        )
    }
}
