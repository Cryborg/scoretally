package com.scoretally.ui.matches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.domain.model.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMatchScreen(
    onMatchSaved: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: AddMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved, uiState.createdMatchId) {
        if (uiState.isSaved && uiState.createdMatchId != null) {
            onMatchSaved(uiState.createdMatchId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_match_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded && !uiState.isSaving }
            ) {
                OutlinedTextField(
                    value = uiState.games.find { it.id == uiState.selectedGameId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.add_match_game_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !uiState.isSaving,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.games.forEach { game ->
                        DropdownMenuItem(
                            text = { Text(game.name) },
                            onClick = {
                                viewModel.onGameSelected(game.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.add_match_players_label),
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.players.isEmpty()) {
                Text(
                    text = stringResource(R.string.add_match_players_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                uiState.players.forEach { player ->
                    PlayerCheckbox(
                        player = player,
                        isSelected = player.id in uiState.selectedPlayerIds,
                        onToggle = { viewModel.onPlayerToggled(player.id) },
                        enabled = !uiState.isSaving
                    )
                }
            }

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text(stringResource(R.string.notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !uiState.isSaving
            )

            Button(
                onClick = { viewModel.saveMatch() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving && uiState.selectedGameId != null && uiState.selectedPlayerIds.isNotEmpty()
            ) {
                Text(
                    if (uiState.isSaving) stringResource(R.string.saving)
                    else stringResource(R.string.add_match_create_button)
                )
            }
        }
    }
}

@Composable
fun PlayerCheckbox(
    player: Player,
    isSelected: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = player.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
