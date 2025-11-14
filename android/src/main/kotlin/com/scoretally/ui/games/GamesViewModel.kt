package com.scoretally.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Game
import com.scoretally.domain.repository.GameRepository
import com.scoretally.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly

    val games: StateFlow<List<Game>> = combine(
        gameRepository.getAllGames(),
        _showFavoritesOnly
    ) { gamesList, favoritesOnly ->
        val filteredList = if (favoritesOnly) {
            gamesList.filter { it.isFavorite }
        } else {
            gamesList
        }
        // Tri : d'abord "Bientôt disponible", puis jeux personnalisés (non prédéfinis), puis jeux prédéfinis
        filteredList.sortedWith(compareBy({ it.isComingSoon }, { it.isPredefined }, { it.name }))
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            gameRepository.deleteGame(game)
        }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            gameRepository.updateGame(game.copy(isFavorite = !game.isFavorite))
        }
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }
}
