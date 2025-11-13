package com.scoretally.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.domain.model.PlayerScore
import com.scoretally.ui.components.toComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    onBack: () -> Unit,
    viewModel: MatchDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.matchDetails?.game?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back))
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
                    Text(uiState.error ?: "Error")
                }
            }

            uiState.matchDetails != null -> {
                val scoreIncrement = uiState.matchDetails!!.game.scoreIncrement
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.matchDetails!!.playerScores) { playerScore ->
                        PlayerScoreItem(
                            playerScore = playerScore,
                            onIncrement = { viewModel.incrementScore(playerScore.matchPlayer, scoreIncrement) },
                            onDecrement = { viewModel.decrementScore(playerScore.matchPlayer, scoreIncrement) },
                            onScoreEdit = { newScore ->
                                viewModel.updateScore(playerScore.matchPlayer, newScore)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerScoreItem(
    playerScore: PlayerScore,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onScoreEdit: (Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(playerScore.player.preferredColor.toComposeColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = playerScore.player.name,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalIconButton(
                    onClick = onDecrement,
                    enabled = playerScore.matchPlayer.score > 0
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (playerScore.matchPlayer.score > 0)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                Surface(
                    modifier = Modifier
                        .widthIn(min = 80.dp)
                        .clickable { showEditDialog = true },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = playerScore.matchPlayer.score.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }

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
            currentScore = playerScore.matchPlayer.score,
            playerName = playerScore.player.name,
            onDismiss = { showEditDialog = false },
            onConfirm = { newScore ->
                onScoreEdit(newScore)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditScoreDialog(
    currentScore: Int,
    playerName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var scoreText by remember { mutableStateOf(currentScore.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.match_detail_edit_score)) },
        text = {
            Column {
                Text(playerName, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.match_detail_enter_score)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newScore = scoreText.toIntOrNull() ?: currentScore
                    onConfirm(newScore)
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
