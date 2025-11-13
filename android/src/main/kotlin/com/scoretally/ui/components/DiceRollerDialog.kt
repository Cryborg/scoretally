package com.scoretally.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scoretally.R
import kotlin.random.Random

@Composable
fun DiceRollerDialog(
    diceCount: Int,
    diceFaces: Int,
    onDismiss: () -> Unit
) {
    var currentDiceCount by remember { mutableStateOf(diceCount) }
    var currentDiceFaces by remember { mutableStateOf(diceFaces) }
    var results by remember { mutableStateOf<List<Int>>(emptyList()) }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tools_dice_roller)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NumberField(
                    value = currentDiceCount,
                    onValueChange = { currentDiceCount = it },
                    label = stringResource(R.string.dice_number),
                    modifier = Modifier.fillMaxWidth(),
                    minValue = 1,
                    maxValue = 10
                )
                NumberField(
                    value = currentDiceFaces,
                    onValueChange = { currentDiceFaces = it },
                    label = stringResource(R.string.dice_faces),
                    modifier = Modifier.fillMaxWidth(),
                    minValue = 2,
                    maxValue = 100
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        results = List(currentDiceCount) { Random.nextInt(1, currentDiceFaces + 1) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.dice_roll))
                }

                if (results.isNotEmpty()) {
                    HorizontalDivider()
                    Text(
                        text = stringResource(R.string.dice_result),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        results.forEach { result ->
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
                    if (results.size > 1) {
                        Text(
                            text = "${stringResource(R.string.dice_total)}: ${results.sum()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Composable
fun FirstPlayerDialog(
    players: List<String>,
    onDismiss: () -> Unit
) {
    val winner by remember { mutableStateOf(players.randomOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (players.isEmpty()) {
                    Text(stringResource(R.string.add_match_players_empty))
                } else if (winner != null) {
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
                                text = stringResource(R.string.first_player_result, winner!!),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
