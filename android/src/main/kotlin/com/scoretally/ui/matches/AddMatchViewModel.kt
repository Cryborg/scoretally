package com.scoretally.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Game
import com.scoretally.domain.model.Match
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.GameRepository
import com.scoretally.domain.repository.PlayerRepository
import com.scoretally.domain.usecase.CreateMatchWithPlayersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class AddMatchUiState(
    val games: List<Game> = emptyList(),
    val players: List<Player> = emptyList(),
    val selectedGameId: Long? = null,
    val selectedPlayerIds: Set<Long> = emptySet(),
    val notes: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val createdMatchId: Long? = null
)

@HiltViewModel
class AddMatchViewModel @Inject constructor(
    gameRepository: GameRepository,
    playerRepository: PlayerRepository,
    private val createMatchWithPlayersUseCase: CreateMatchWithPlayersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMatchUiState())
    val uiState: StateFlow<AddMatchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                gameRepository.getAllGames(),
                playerRepository.getAllPlayers()
            ) { games, players ->
                _uiState.value.copy(games = games, players = players)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onGameSelected(gameId: Long?) {
        _uiState.value = _uiState.value.copy(selectedGameId = gameId)
    }

    fun onPlayerToggled(playerId: Long) {
        val currentSelection = _uiState.value.selectedPlayerIds
        _uiState.value = _uiState.value.copy(
            selectedPlayerIds = if (playerId in currentSelection) {
                currentSelection - playerId
            } else {
                currentSelection + playerId
            }
        )
    }

    fun onNotesChange(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveMatch() {
        val state = _uiState.value

        if (state.selectedGameId == null || state.selectedPlayerIds.isEmpty()) return

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val match = Match(
                    gameId = state.selectedGameId,
                    date = LocalDateTime.now(),
                    duration = null,
                    notes = state.notes,
                    isCompleted = false
                )
                val matchId = createMatchWithPlayersUseCase(match, state.selectedPlayerIds.toList())
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true,
                    createdMatchId = matchId
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }
}
