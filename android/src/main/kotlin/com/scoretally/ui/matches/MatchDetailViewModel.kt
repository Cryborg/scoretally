package com.scoretally.ui.matches

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.MatchPlayer
import com.scoretally.domain.model.MatchWithDetails
import com.scoretally.domain.usecase.GetMatchWithDetailsUseCase
import com.scoretally.domain.usecase.UpdatePlayerScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchDetailUiState(
    val matchDetails: MatchWithDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMatchWithDetailsUseCase: GetMatchWithDetailsUseCase,
    private val updatePlayerScoreUseCase: UpdatePlayerScoreUseCase
) : ViewModel() {

    private val matchId: Long = savedStateHandle.get<Long>("matchId") ?: 0L

    private val _uiState = MutableStateFlow(MatchDetailUiState())
    val uiState: StateFlow<MatchDetailUiState> = _uiState.asStateFlow()

    init {
        loadMatchDetails()
    }

    private fun loadMatchDetails() {
        viewModelScope.launch {
            getMatchWithDetailsUseCase(matchId).collect { details ->
                _uiState.value = _uiState.value.copy(
                    matchDetails = details,
                    isLoading = false,
                    error = if (details == null) "Match not found" else null
                )
            }
        }
    }

    fun updateScore(matchPlayer: MatchPlayer, newScore: Int) {
        viewModelScope.launch {
            updatePlayerScoreUseCase(matchPlayer, newScore)
        }
    }

    fun incrementScore(matchPlayer: MatchPlayer, increment: Int = 1) {
        updateScore(matchPlayer, matchPlayer.score + increment)
    }

    fun decrementScore(matchPlayer: MatchPlayer, decrement: Int = 1) {
        updateScore(matchPlayer, maxOf(0, matchPlayer.score - decrement))
    }
}
