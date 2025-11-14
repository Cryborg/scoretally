package com.scoretally.ui.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.ui.components.FormScaffold
import com.scoretally.ui.components.GameForm

@Composable
fun AddGameScreen(
    onGameSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FormScaffold(
        title = stringResource(R.string.add_game_title),
        onBack = onBack,
        isSaved = uiState.isSaved,
        onSaved = onGameSaved
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
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
            onSearchBgg = { viewModel.searchBgg(uiState.name) },
            modifier = Modifier,
            isPredefined = false
            )

            // Modale de recherche BGG
            if (uiState.isSearchingBgg || uiState.bggSearchResults != null || uiState.bggSearchError != null) {
                BggSearchDialog(
                    isSearching = uiState.isSearchingBgg,
                    searchResults = uiState.bggSearchResults,
                    error = uiState.bggSearchError,
                    onSelectGame = viewModel::selectBggGame,
                    onDismiss = viewModel::dismissBggSearch
                )
            }
        }
    }
}

@Composable
fun BggSearchDialog(
    isSearching: Boolean,
    searchResults: List<com.scoretally.data.remote.bgg.dto.BggSearchItem>?,
    error: String?,
    onSelectGame: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (!isSearching) onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .padding(16.dp)
        ) {
            when {
                isSearching -> {
                    // Spinner de chargement
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.search_bgg),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
                error != null -> {
                    // Affichage d'erreur
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
                searchResults != null -> {
                    // Liste des résultats
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.search_bgg),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (searchResults.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_results_found),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f, fill = false),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(searchResults) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectGame(item.id) }
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = item.name?.value ?: "Unknown",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            item.yearPublished?.value?.let { year ->
                                                Text(
                                                    text = year,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 8.dp)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        }
    }
}
