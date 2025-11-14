package com.scoretally.ui.players

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.ui.components.FormScaffold
import com.scoretally.ui.components.PlayerForm

@Composable
fun AddPlayerScreen(
    onPlayerSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FormScaffold(
        title = stringResource(R.string.add_player_title),
        onBack = onBack,
        isSaved = uiState.isSaved,
        onSaved = onPlayerSaved
    ) { padding ->
        PlayerForm(
            name = uiState.name,
            onNameChange = viewModel::onNameChange,
            preferredColor = uiState.preferredColor,
            onColorChange = viewModel::onColorChange,
            onSave = viewModel::savePlayer,
            isSaving = uiState.isSaving,
            canSave = uiState.name.isNotBlank(),
            saveButtonText = stringResource(R.string.save),
            modifier = Modifier.padding(padding),
            nameError = uiState.nameError
        )
    }
}
