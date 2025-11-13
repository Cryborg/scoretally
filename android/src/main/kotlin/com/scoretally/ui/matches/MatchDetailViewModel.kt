package com.scoretally.ui.matches

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.GridType
import com.scoretally.domain.model.MatchPlayer
import com.scoretally.domain.model.MatchWithDetails
import com.scoretally.domain.model.scoregrid.GridStateSerializer
import com.scoretally.domain.model.scoregrid.ScoreGrid
import com.scoretally.domain.model.scoregrid.YahtzeeGrid
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.usecase.GetMatchWithDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchDetailUiState(
    val matchDetails: MatchWithDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val grids: Map<Long, ScoreGrid> = emptyMap()
)

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMatchWithDetailsUseCase: GetMatchWithDetailsUseCase,
    private val matchPlayerRepository: MatchPlayerRepository
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
                val grids = if (details?.game?.gridType == GridType.YAHTZEE) {
                    details.playerScores.associate { playerScore ->
                        val matchPlayer = playerScore.matchPlayer
                        val grid = YahtzeeGrid()
                        val savedState = GridStateSerializer.deserializeFromJson(matchPlayer.gridState)
                        val loadedGrid = GridStateSerializer.applyStateToGrid(grid, savedState)
                        matchPlayer.id to loadedGrid
                    }
                } else {
                    emptyMap()
                }

                _uiState.value = _uiState.value.copy(
                    matchDetails = details,
                    grids = grids,
                    isLoading = false,
                    error = if (details == null) "Match not found" else null
                )
            }
        }
    }

    fun updateScore(matchPlayer: MatchPlayer, newScore: Int) {
        viewModelScope.launch {
            matchPlayerRepository.updateMatchPlayer(
                matchPlayer.copy(score = newScore)
            )
        }
    }

    fun incrementScore(matchPlayer: MatchPlayer, increment: Int = 1) {
        updateScore(matchPlayer, matchPlayer.score + increment)
    }

    fun decrementScore(matchPlayer: MatchPlayer, decrement: Int = 1) {
        updateScore(matchPlayer, maxOf(0, matchPlayer.score - decrement))
    }

    fun updateGridCell(matchPlayerId: Long, cellId: String, value: Int?) {
        viewModelScope.launch {
            val currentGrid = _uiState.value.grids[matchPlayerId] ?: return@launch
            val updatedGrid = currentGrid.updateCell(cellId, value)

            // Mettre à jour l'état local
            _uiState.value = _uiState.value.copy(
                grids = _uiState.value.grids + (matchPlayerId to updatedGrid)
            )

            // Calculer le nouveau score total
            val newScore = updatedGrid.calculateTotal()
            val matchPlayer = _uiState.value.matchDetails?.playerScores
                ?.find { it.matchPlayer.id == matchPlayerId }?.matchPlayer
                ?: return@launch

            // Sérialiser l'état de la grille
            val gridStateJson = GridStateSerializer.serializeToJson(updatedGrid.cells)

            // Sauvegarder dans la base de données
            matchPlayerRepository.updateMatchPlayer(
                matchPlayer.copy(
                    score = newScore,
                    gridState = gridStateJson
                )
            )
        }
    }
}
