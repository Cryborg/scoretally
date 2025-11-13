package com.scoretally.ui.players

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.ui.components.PlayerForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerScreen(
    onPlayerSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onPlayerSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_player_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
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
            modifier = Modifier.padding(padding)
        )
    }
}
