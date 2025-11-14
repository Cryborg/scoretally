package com.scoretally.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import com.scoretally.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val players: StateFlow<List<Player>> = playerRepository.getAllPlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    fun deletePlayer(playerId: Long) {
        viewModelScope.launch {
            playerRepository.deletePlayerById(playerId)
        }
    }
}
