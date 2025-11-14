package com.scoretally.ui.matches

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.GridType
import com.scoretally.domain.model.MatchPlayer
import com.scoretally.domain.model.MatchWithDetails
import com.scoretally.domain.model.scoregrid.GridStateSerializer
import com.scoretally.domain.model.scoregrid.RummyGrid
import com.scoretally.domain.model.scoregrid.ScoreGrid
import com.scoretally.domain.model.scoregrid.TarotGrid
import com.scoretally.domain.model.scoregrid.YahtzeeGrid
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.usecase.GetMatchWithDetailsUseCase
import com.scoretally.util.Logger
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
    private val application: Application,
    private val getMatchWithDetailsUseCase: GetMatchWithDetailsUseCase,
    private val matchPlayerRepository: MatchPlayerRepository,
    private val logger: Logger
) : ViewModel() {

    private val matchId: Long = savedStateHandle.get<Long>("matchId") ?: 0L

    private val _uiState = MutableStateFlow(MatchDetailUiState())
    val uiState: StateFlow<MatchDetailUiState> = _uiState.asStateFlow()

    init {
        loadMatchDetails()
    }

    private fun loadMatchDetails() {
        viewModelScope.launch {
            try {
                logger.log("MatchDetailViewModel: Loading match $matchId")

                getMatchWithDetailsUseCase(matchId).collect { details ->
                    val grids = when (details?.game?.gridType) {
                        GridType.YAHTZEE -> {
                            details.playerScores.associate { playerScore ->
                                val matchPlayer = playerScore.matchPlayer
                                val initialCells = YahtzeeGrid.createInitialCells(application)
                                val grid = YahtzeeGrid(cells = initialCells)
                                val savedState = GridStateSerializer.deserializeFromJson(matchPlayer.gridState)
                                val loadedGrid = GridStateSerializer.applyStateToGrid(grid, savedState)
                                matchPlayer.id to loadedGrid
                            }
                        }
                        GridType.TAROT -> {
                            details.playerScores.associate { playerScore ->
                                val matchPlayer = playerScore.matchPlayer
                                val grid = TarotGrid.createInitialGrid(application)
                                val savedState = GridStateSerializer.deserializeFromJson(matchPlayer.gridState)
                                val loadedGrid = GridStateSerializer.applyStateToGrid(grid, savedState)
                                matchPlayer.id to loadedGrid
                            }
                        }
                        GridType.RUMMY -> {
                            details.playerScores.associate { playerScore ->
                                val matchPlayer = playerScore.matchPlayer
                                val grid = RummyGrid.createInitialGrid(application)
                                val savedState = GridStateSerializer.deserializeFromJson(matchPlayer.gridState)
                                val loadedGrid = GridStateSerializer.applyStateToGrid(grid, savedState)
                                matchPlayer.id to loadedGrid
                            }
                        }
                        else -> emptyMap()
                    }

                    _uiState.value = _uiState.value.copy(
                        matchDetails = details,
                        grids = grids,
                        isLoading = false,
                        error = if (details == null) "Match not found" else null
                    )

                    if (details != null) {
                        logger.log("MatchDetailViewModel: Match loaded successfully - Game: ${details.game.name}")
                    } else {
                        logger.log("MatchDetailViewModel: Match $matchId not found")
                    }
                }
            } catch (e: Exception) {
                logger.recordException(e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading match: ${e.message}"
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
        val newScore = matchPlayer.score - decrement
        val game = _uiState.value.matchDetails?.game
        val finalScore = if (game?.allowNegativeScores == false) {
            maxOf(0, newScore)
        } else {
            newScore
        }
        updateScore(matchPlayer, finalScore)
    }

    fun updateGridCell(matchPlayerId: Long, cellId: String, value: Int?) {
        viewModelScope.launch {
            try {
                logger.log("MatchDetailViewModel: Updating cell $cellId for player $matchPlayerId with value $value")

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

                logger.log("MatchDetailViewModel: Cell updated successfully, new score: $newScore")
            } catch (e: Exception) {
                logger.recordException(e)
                logger.log("MatchDetailViewModel: Error updating cell: ${e.message}")
            }
        }
    }

    fun addTarotRound() {
        viewModelScope.launch {
            try {
                logger.log("MatchDetailViewModel: Adding Tarot round")

                val updatedGrids = _uiState.value.grids.mapValues { (matchPlayerId, grid) ->
                    if (grid is TarotGrid) {
                        val updatedGrid = grid.addRound(application)

                        // Sauvegarder l'état de la grille
                        val gridStateJson = GridStateSerializer.serializeToJson(updatedGrid.cells)
                        val matchPlayer = _uiState.value.matchDetails?.playerScores
                            ?.find { it.matchPlayer.id == matchPlayerId }?.matchPlayer

                        if (matchPlayer != null) {
                            matchPlayerRepository.updateMatchPlayer(
                                matchPlayer.copy(gridState = gridStateJson)
                            )
                        }

                        updatedGrid
                    } else {
                        grid
                    }
                }

                _uiState.value = _uiState.value.copy(grids = updatedGrids)
                logger.log("MatchDetailViewModel: Tarot round added successfully")
            } catch (e: Exception) {
                logger.recordException(e)
                logger.log("MatchDetailViewModel: Error adding Tarot round: ${e.message}")
            }
        }
    }

    fun addRummyRound() {
        viewModelScope.launch {
            try {
                logger.log("MatchDetailViewModel: Adding Rummy round")

                val updatedGrids = _uiState.value.grids.mapValues { (matchPlayerId, grid) ->
                    if (grid is RummyGrid) {
                        val updatedGrid = grid.addRound(application)

                        // Sauvegarder l'état de la grille
                        val gridStateJson = GridStateSerializer.serializeToJson(updatedGrid.cells)
                        val matchPlayer = _uiState.value.matchDetails?.playerScores
                            ?.find { it.matchPlayer.id == matchPlayerId }?.matchPlayer

                        if (matchPlayer != null) {
                            matchPlayerRepository.updateMatchPlayer(
                                matchPlayer.copy(gridState = gridStateJson)
                            )
                        }

                        updatedGrid
                    } else {
                        grid
                    }
                }

                _uiState.value = _uiState.value.copy(grids = updatedGrids)
                logger.log("MatchDetailViewModel: Rummy round added successfully")
            } catch (e: Exception) {
                logger.recordException(e)
                logger.log("MatchDetailViewModel: Error adding Rummy round: ${e.message}")
            }
        }
    }
}
