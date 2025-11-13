package com.scoretally.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.scoretally.R
import com.scoretally.domain.model.GridType

@Composable
fun GameForm(
    name: String,
    onNameChange: (String) -> Unit,
    minPlayers: String,
    onMinPlayersChange: (String) -> Unit,
    maxPlayers: String,
    onMaxPlayersChange: (String) -> Unit,
    averageDuration: String,
    onAverageDurationChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    scoreIncrement: String,
    onScoreIncrementChange: (String) -> Unit,
    hasDice: Boolean,
    onHasDiceChange: (Boolean) -> Unit,
    diceCount: String,
    onDiceCountChange: (String) -> Unit,
    diceFaces: String,
    onDiceFacesChange: (String) -> Unit,
    allowNegativeScores: Boolean,
    onAllowNegativeScoresChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean,
    canSave: Boolean,
    saveButtonText: String,
    modifier: Modifier = Modifier,
    isPredefined: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.add_game_name_label)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = minPlayers,
                onValueChange = onMinPlayersChange,
                label = { Text(stringResource(R.string.add_game_min_players_label)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = maxPlayers,
                onValueChange = onMaxPlayersChange,
                label = { Text(stringResource(R.string.add_game_max_players_label)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isSaving
            )
        }

        OutlinedTextField(
            value = averageDuration,
            onValueChange = onAverageDurationChange,
            label = { Text(stringResource(R.string.add_game_duration_label)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isSaving
        )

        OutlinedTextField(
            value = category,
            onValueChange = onCategoryChange,
            label = { Text(stringResource(R.string.add_game_category_label)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.add_game_description_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = scoreIncrement,
            onValueChange = onScoreIncrementChange,
            label = { Text(stringResource(R.string.add_game_score_increment_label)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isSaving
        )

        // Dice checkbox
        if (!isPredefined) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.game_has_dice_label),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(R.string.game_has_dice_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = hasDice,
                    onCheckedChange = onHasDiceChange,
                    enabled = !isSaving
                )
            }

            if (hasDice) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = diceCount,
                        onValueChange = onDiceCountChange,
                        label = { Text(stringResource(R.string.add_game_dice_count_label)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isSaving
                    )

                    OutlinedTextField(
                        value = diceFaces,
                        onValueChange = onDiceFacesChange,
                        label = { Text(stringResource(R.string.add_game_dice_faces_label)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isSaving
                    )
                }
            }
        }

        // Allow negative scores switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Autoriser les scores négatifs",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Les joueurs pourront avoir des scores < 0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = allowNegativeScores,
                onCheckedChange = onAllowNegativeScoresChange,
                enabled = !isSaving
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving && canSave
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(saveButtonText)
            }
        }
    }
}
