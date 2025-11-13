package com.scoretally.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scoretally.R
import com.scoretally.domain.model.Player
import com.scoretally.ui.components.NumberField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onBack: () -> Unit,
    viewModel: ToolsViewModel = hiltViewModel()
) {
    val diceRollResult by viewModel.diceRollResult.collectAsState()
    val firstPlayerState by viewModel.firstPlayerState.collectAsState()
    val players by viewModel.players.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tools_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                DiceRollerCard(
                    diceRollResult = diceRollResult,
                    onDiceCountChange = viewModel::updateDiceCount,
                    onDiceFacesChange = viewModel::updateDiceFaces,
                    onRollDice = viewModel::rollDice
                )
            }

            item {
                FirstPlayerCard(
                    players = players,
                    firstPlayerState = firstPlayerState,
                    onPlayerToggle = viewModel::togglePlayerSelection,
                    onDraw = viewModel::drawFirstPlayer,
                    onClear = viewModel::clearFirstPlayerDraw
                )
            }
        }
    }
}

@Composable
private fun DiceRollerCard(
    diceRollResult: DiceRollResult,
    onDiceCountChange: (Int) -> Unit,
    onDiceFacesChange: (Int) -> Unit,
    onRollDice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.tools_dice_roller),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NumberField(
                    value = diceRollResult.diceCount,
                    onValueChange = onDiceCountChange,
                    label = stringResource(R.string.dice_number),
                    modifier = Modifier.weight(1f),
                    minValue = 1,
                    maxValue = 10
                )

                NumberField(
                    value = diceRollResult.diceFaces,
                    onValueChange = onDiceFacesChange,
                    label = stringResource(R.string.dice_faces),
                    modifier = Modifier.weight(1f),
                    minValue = 2,
                    maxValue = 100
                )
            }

            Button(
                onClick = onRollDice,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.dice_roll))
            }

            if (diceRollResult.results.isNotEmpty()) {
                Divider()

                Text(
                    text = stringResource(R.string.dice_result),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    diceRollResult.results.forEach { result ->
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = result.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (diceRollResult.results.size > 1) {
                    Text(
                        text = "${stringResource(R.string.dice_total)}: ${diceRollResult.total}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FirstPlayerCard(
    players: List<Player>,
    firstPlayerState: FirstPlayerState,
    onPlayerToggle: (Long) -> Unit,
    onDraw: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.tools_first_player),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (players.isEmpty()) {
                Text(
                    text = stringResource(R.string.add_match_players_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = stringResource(R.string.first_player_select),
                    style = MaterialTheme.typography.bodyMedium
                )

                players.forEach { player ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = player.id in firstPlayerState.selectedPlayerIds,
                            onCheckedChange = { onPlayerToggle(player.id) }
                        )
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDraw,
                        enabled = firstPlayerState.selectedPlayerIds.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.first_player_draw))
                    }

                    if (firstPlayerState.winner != null) {
                        OutlinedButton(
                            onClick = onClear,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }

                if (firstPlayerState.winner != null) {
                    Divider()

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.first_player_result,
                                    firstPlayerState.winner!!.name
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
