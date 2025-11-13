package com.scoretally.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class DiceRollResult(
    val diceCount: Int = 1,
    val diceFaces: Int = 6,
    val results: List<Int> = emptyList(),
    val total: Int = 0
)

data class FirstPlayerState(
    val selectedPlayerIds: Set<Long> = emptySet(),
    val winner: Player? = null
)

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val players: StateFlow<List<Player>> = playerRepository.getAllPlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _diceRollResult = MutableStateFlow(DiceRollResult())
    val diceRollResult: StateFlow<DiceRollResult> = _diceRollResult.asStateFlow()

    private val _firstPlayerState = MutableStateFlow(FirstPlayerState())
    val firstPlayerState: StateFlow<FirstPlayerState> = _firstPlayerState.asStateFlow()

    fun updateDiceCount(count: Int) {
        _diceRollResult.value = _diceRollResult.value.copy(diceCount = count.coerceIn(1, 10))
    }

    fun updateDiceFaces(faces: Int) {
        _diceRollResult.value = _diceRollResult.value.copy(diceFaces = faces.coerceIn(2, 100))
    }

    fun rollDice() {
        val current = _diceRollResult.value
        val results = List(current.diceCount) { Random.nextInt(1, current.diceFaces + 1) }
        val total = results.sum()
        _diceRollResult.value = current.copy(results = results, total = total)
    }

    fun togglePlayerSelection(playerId: Long) {
        val currentSelection = _firstPlayerState.value.selectedPlayerIds
        val newSelection = if (playerId in currentSelection) {
            currentSelection - playerId
        } else {
            currentSelection + playerId
        }
        _firstPlayerState.value = _firstPlayerState.value.copy(
            selectedPlayerIds = newSelection,
            winner = null
        )
    }

    fun drawFirstPlayer() {
        val selectedPlayers = players.value.filter { it.id in _firstPlayerState.value.selectedPlayerIds }
        if (selectedPlayers.isNotEmpty()) {
            val winner = selectedPlayers.random()
            _firstPlayerState.value = _firstPlayerState.value.copy(winner = winner)
        }
    }

    fun clearFirstPlayerDraw() {
        _firstPlayerState.value = FirstPlayerState()
    }
}
