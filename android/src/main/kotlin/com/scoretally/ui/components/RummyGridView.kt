package com.scoretally.ui.components

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.scoretally.R
import com.scoretally.domain.model.PlayerScore
import com.scoretally.domain.model.scoregrid.RummyGrid

data class PlayerRummyGrid(
    val playerScore: PlayerScore,
    val grid: RummyGrid
)

@Composable
fun RummyMultiPlayerGridView(
    playerGrids: List<PlayerRummyGrid>,
    onCellUpdate: (Long, String, Int?) -> Unit,
    onAddRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (playerGrids.isEmpty()) return

    val firstGrid = playerGrids.first().grid
    val allRounds = firstGrid.cells
    var editingCell by remember { mutableStateOf<Pair<Long, String>?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // En-tête fixe avec les noms des joueurs
        Row(modifier = Modifier.fillMaxWidth()) {
            // Colonne des manches (vide pour l'en-tête)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(80.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
            )

            // En-têtes des joueurs
            playerGrids.forEach { playerGrid ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .background(playerGrid.playerScore.player.preferredColor.toComposeColor().copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = playerGrid.playerScore.player.name,
                        modifier = Modifier.rotate(-90f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Contenu scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Toutes les manches
            allRounds.forEach { round ->
                RummyRoundRow(
                    round = round,
                    playerGrids = playerGrids,
                    onCellUpdate = onCellUpdate,
                    editingCell = editingCell,
                    onEditingCellChange = { editingCell = it }
                )
            }
        }

        // Ligne de total final
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.rummy_total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            playerGrids.forEach { playerGrid ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = playerGrid.grid.calculateTotal().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Bouton ajouter une manche
        Button(
            onClick = onAddRound,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.rummy_add_round))
        }
    }
}

@Composable
private fun RummyRoundRow(
    round: com.scoretally.domain.model.GridCell,
    playerGrids: List<PlayerRummyGrid>,
    onCellUpdate: (Long, String, Int?) -> Unit,
    editingCell: Pair<Long, String>?,
    onEditingCellChange: (Pair<Long, String>?) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Colonne du label (numéro de manche)
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(48.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = round.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Colonnes des joueurs
        playerGrids.forEach { playerGrid ->
            val cell = playerGrid.grid.cells.find { it.id == round.id }
            val matchPlayerId = playerGrid.playerScore.matchPlayer.id
            val isEditing = editingCell == Pair(matchPlayerId, round.id)

            RummyCell(
                cell = cell,
                isEditing = isEditing,
                onStartEdit = {
                    if (cell?.isLocked == false) {
                        onEditingCellChange(Pair(matchPlayerId, round.id))
                    }
                },
                onValueChange = { value ->
                    onCellUpdate(matchPlayerId, round.id, value)
                    onEditingCellChange(null)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RummyCell(
    cell: com.scoretally.domain.model.GridCell?,
    isEditing: Boolean,
    onStartEdit: () -> Unit,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val view = LocalView.current

    Box(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .combinedClickable(
                enabled = !isEditing,
                onClick = { onStartEdit() },
                onLongClick = {
                    if (cell?.value != null) {
                        onValueChange(null)
                    }
                }
            )
            .then(if (isEditing) Modifier else Modifier.padding(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Mode édition -> afficher EditText natif
            isEditing -> {
                AndroidView(
                    factory = { ctx ->
                        EditText(ctx).apply {
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                            imeOptions = EditorInfo.IME_ACTION_DONE
                            gravity = Gravity.CENTER
                            textSize = 16f
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(textColor)
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            setText(cell?.value?.toString() ?: "")

                            setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    val value = text.toString().toIntOrNull()
                                    onValueChange(value)
                                    clearFocus()
                                    val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                    imm?.hideSoftInputFromWindow(windowToken, 0)
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                    },
                    update = { editText ->
                        editText.requestFocus()
                        editText.post {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Mode affichage normal
            else -> {
                Text(
                    text = cell?.value?.toString() ?: "—",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (cell?.value != null) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        cell?.value == null -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        cell.value!! < 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
