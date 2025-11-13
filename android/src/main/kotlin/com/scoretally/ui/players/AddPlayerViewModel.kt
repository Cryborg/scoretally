package com.scoretally.ui.players

import android.content.Context
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

data class AddPlayerUiState(
    val name: String = "",
    val preferredColor: String = "#6750A4",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val nameError: String? = null
)

@HiltViewModel
class AddPlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlayerUiState())
    val uiState: StateFlow<AddPlayerUiState> = _uiState.asStateFlow()

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
                val nameExists = existingPlayers.any { it.name.equals(trimmedName, ignoreCase = true) }

                if (nameExists) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        nameError = context.getString(R.string.error_player_name_exists)
                    )
                    return@launch
                }

                val player = Player(
                    name = trimmedName,
                    preferredColor = state.preferredColor
                )
                playerRepository.insertPlayer(player)
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }
}
