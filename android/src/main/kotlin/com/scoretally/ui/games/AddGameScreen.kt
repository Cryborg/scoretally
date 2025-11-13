package com.scoretally.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.ui.components.GameForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    onGameSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onGameSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_game_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
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
            onSave = viewModel::saveGame,
            isSaving = uiState.isSaving,
            canSave = uiState.name.isNotBlank(),
            saveButtonText = stringResource(R.string.save),
            modifier = Modifier.padding(padding),
            isPredefined = false
        )
    }
}
