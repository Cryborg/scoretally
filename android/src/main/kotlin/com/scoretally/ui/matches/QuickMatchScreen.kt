package com.scoretally.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMatchScreen(
    onBack: () -> Unit,
    viewModel: QuickMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var focusedPlayerId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(uiState.players.size) {
        if (uiState.players.size > 2) {
            val lastPlayer = uiState.players.lastOrNull()
            if (lastPlayer != null) {
                focusedPlayerId = lastPlayer.id
                delay(100)
                listState.animateScrollToItem(uiState.players.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.match_create_quick)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addPlayer() }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add))
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = uiState.players,
                key = { it.id }
            ) { player ->
                QuickPlayerItem(
                    player = player,
                    availablePlayers = uiState.availablePlayers,
                    shouldFocus = focusedPlayerId == player.id,
                    onFocused = { focusedPlayerId = null },
                    onNameChange = { name, playerId -> viewModel.updatePlayerName(player.id, name, playerId) },
                    onIncrement = { viewModel.incrementScore(player.id) },
                    onDecrement = { viewModel.decrementScore(player.id) },
                    onScoreEdit = { newScore -> viewModel.updateScore(player.id, newScore) },
                    onRemove = { viewModel.removePlayer(player.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPlayerItem(
    player: QuickPlayer,
    availablePlayers: List<com.scoretally.domain.model.Player>,
    shouldFocus: Boolean,
    onFocused: () -> Unit,
    onNameChange: (String, Long?) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onScoreEdit: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var textValue by remember(player.name) { mutableStateOf(player.name) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(shouldFocus) {
        if (shouldFocus) {
            delay(300)
            focusRequester.requestFocus()
            keyboardController?.show()
            onFocused()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Player name row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = {
                            textValue = it
                            onNameChange(it, null)
                        },
                        label = { Text(stringResource(R.string.add_player_name_label)) },
                        placeholder = { Text("Joueur ${player.id}") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true
                    )

                    if (availablePlayers.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            availablePlayers.forEach { availablePlayer ->
                                DropdownMenuItem(
                                    text = { Text(availablePlayer.name) },
                                    onClick = {
                                        textValue = availablePlayer.name
                                        onNameChange(availablePlayer.name, availablePlayer.id)
                                        expanded = false
                                        keyboardController?.hide()
                                    }
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalIconButton(
                    onClick = onDecrement,
                    enabled = player.score > 0
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (player.score > 0)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    modifier = Modifier
                        .widthIn(min = 80.dp)
                        .clickable { showEditDialog = true },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = player.score.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                FilledTonalIconButton(onClick = onIncrement) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_increment),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        EditScoreDialog(
            currentScore = player.score,
            playerName = if (player.name.isNotBlank()) player.name else "Joueur ${player.id}",
            onDismiss = { showEditDialog = false },
            onConfirm = { newScore ->
                onScoreEdit(newScore)
                showEditDialog = false
            }
        )
    }
}
