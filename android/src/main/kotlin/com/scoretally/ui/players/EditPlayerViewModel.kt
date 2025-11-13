package com.scoretally.ui.players

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.R
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditPlayerUiState(
    val playerId: Long = 0,
    val name: String = "",
    val preferredColor: String = "#6750A4",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val nameError: String? = null
)

@HiltViewModel
class EditPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playerRepository: PlayerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val playerId: Long = checkNotNull(savedStateHandle["playerId"])

    private val _uiState = MutableStateFlow(EditPlayerUiState(playerId = playerId))
    val uiState: StateFlow<EditPlayerUiState> = _uiState.asStateFlow()

    init {
        loadPlayer()
    }

    private fun loadPlayer() {
        viewModelScope.launch {
            try {
                val player = playerRepository.getPlayerById(playerId)
                if (player != null) {
                    _uiState.value = _uiState.value.copy(
                        name = player.name,
                        preferredColor = player.preferredColor,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Player not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onColorChange(color: String) {
        _uiState.value = _uiState.value.copy(preferredColor = color)
    }

    fun savePlayer() {
        val state = _uiState.value

        if (state.name.isBlank()) return

        _uiState.value = state.copy(isSaving = true, nameError = null)

        viewModelScope.launch {
            try {
                val trimmedName = state.name.trim()
                val existingPlayers = playerRepository.getAllPlayers().first()
                val nameExists = existingPlayers.any {
                    it.id != state.playerId && it.name.equals(trimmedName, ignoreCase = true)
                }

                if (nameExists) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        nameError = context.getString(R.string.error_player_name_exists)
                    )
                    return@launch
                }

                val player = Player(
                    id = state.playerId,
                    name = trimmedName,
                    preferredColor = state.preferredColor
                )
                playerRepository.updatePlayer(player)
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun deletePlayer() {
        viewModelScope.launch {
            try {
                playerRepository.deletePlayerById(playerId)
                _uiState.value = _uiState.value.copy(isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
