package com.scoretally.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuickPlayer(
    val id: Int,
    val name: String = "",
    val score: Int = 0,
    val selectedPlayerId: Long? = null
)

data class QuickMatchUiState(
    val players: List<QuickPlayer> = listOf(
        QuickPlayer(id = 1),
        QuickPlayer(id = 2)
    ),
    val nextPlayerId: Int = 3,
    val availablePlayers: List<Player> = emptyList()
)

@HiltViewModel
class QuickMatchViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickMatchUiState())
    val uiState: StateFlow<QuickMatchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerRepository.getAllPlayers().collect { players ->
                _uiState.update { it.copy(availablePlayers = players) }
            }
        }
    }

    fun addPlayer() {
        val state = _uiState.value
        val newPlayer = QuickPlayer(id = state.nextPlayerId)
        _uiState.value = state.copy(
            players = state.players + newPlayer,
            nextPlayerId = state.nextPlayerId + 1
        )
    }

    fun updatePlayerName(playerId: Int, name: String, selectedPlayerId: Long? = null) {
        val state = _uiState.value
        _uiState.value = state.copy(
            players = state.players.map { player ->
                if (player.id == playerId) {
                    player.copy(name = name.trim(), selectedPlayerId = selectedPlayerId)
                } else player
            }
        )
    }

    fun removePlayer(playerId: Int) {
        val state = _uiState.value
        _uiState.value = state.copy(
            players = state.players.filter { it.id != playerId }
        )
    }

    fun updateScore(playerId: Int, newScore: Int) {
        val state = _uiState.value
        _uiState.value = state.copy(
            players = state.players.map { player ->
                if (player.id == playerId) player.copy(score = newScore)
                else player
            }
        )
    }

    fun incrementScore(playerId: Int, increment: Int = 1) {
        val state = _uiState.value
        val player = state.players.find { it.id == playerId } ?: return
        updateScore(playerId, player.score + increment)
    }

    fun decrementScore(playerId: Int, decrement: Int = 1) {
        val state = _uiState.value
        val player = state.players.find { it.id == playerId } ?: return
        updateScore(playerId, maxOf(0, player.score - decrement))
    }
}
