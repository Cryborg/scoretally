package com.scoretally.ui.players

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.ui.components.PlayerForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlayerScreen(
    onPlayerSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: EditPlayerViewModel = hiltViewModel()
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
                title = { Text(stringResource(R.string.edit)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
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
                    autoFocus = true,
                    nameError = uiState.nameError
                )
            }
        }
    }
}
