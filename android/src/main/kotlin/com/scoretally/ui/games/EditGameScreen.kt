package com.scoretally.ui.games

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.ui.components.FormScaffold
import com.scoretally.ui.components.GameForm

@Composable
fun EditGameScreen(
    onGameUpdated: () -> Unit,
    onBack: () -> Unit,
    viewModel: EditGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FormScaffold(
        title = stringResource(R.string.edit_game_title),
        onBack = onBack,
        isSaved = uiState.isSaved,
        onSaved = onGameUpdated,
        isLoading = uiState.isLoading
    ) { padding ->
        GameForm(
            name = uiState.name,
            onNameChange = viewModel::onNameChange,
            minPlayers = uiState.minPlayers,
            onMinPlayersChange = viewModel::onMinPlayersChange,
            maxPlayers = uiState.maxPlayers,
            onMaxPlayersChange = viewModel::onMaxPlayersChange,
            averageDuration = uiState.averageDuration,
            onAverageDurationChange = viewModel::onAverageDurationChange,
            category = uiState.category,
            onCategoryChange = viewModel::onCategoryChange,
            description = uiState.description,
            onDescriptionChange = viewModel::onDescriptionChange,
            scoreIncrement = uiState.scoreIncrement,
            onScoreIncrementChange = viewModel::onScoreIncrementChange,
            hasDice = uiState.hasDice,
            onHasDiceChange = viewModel::onHasDiceChange,
            diceCount = uiState.diceCount,
            onDiceCountChange = viewModel::onDiceCountChange,
            diceFaces = uiState.diceFaces,
            onDiceFacesChange = viewModel::onDiceFacesChange,
            allowNegativeScores = uiState.allowNegativeScores,
            onAllowNegativeScoresChange = viewModel::onAllowNegativeScoresChange,
            onSave = viewModel::updateGame,
            isSaving = uiState.isSaving,
            canSave = uiState.name.isNotBlank(),
            saveButtonText = stringResource(R.string.update),
            modifier = Modifier.padding(padding),
            isPredefined = uiState.isPredefined
        )
    }
}
