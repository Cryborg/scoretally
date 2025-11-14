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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.scoretally.R
import com.scoretally.domain.model.PlayerScore
import com.scoretally.domain.model.scoregrid.YahtzeeGrid

data class PlayerGrid(
    val playerScore: PlayerScore,
    val grid: YahtzeeGrid
)

@Composable
fun YahtzeeMultiPlayerGridView(
    playerGrids: List<PlayerGrid>,
    onCellUpdate: (Long, String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (playerGrids.isEmpty()) return

    val firstGrid = playerGrids.first().grid
    val allCategories = firstGrid.cells
    var editingCell by remember { mutableStateOf<Pair<Long, String>?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // En-tête fixe avec les noms des joueurs
        Row(modifier = Modifier.fillMaxWidth()) {
            // Colonne des catégories (vide pour l'en-tête)
            Box(
                modifier = Modifier
                    .width(120.dp)
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

        // Section supérieure
        Text(
            text = stringResource(R.string.yahtzee_upper_section),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        val upperCells = allCategories.filter { it.category == "upper" }
        upperCells.forEach { category ->
            GridRow(
                category = category,
                playerGrids = playerGrids,
                onCellUpdate = onCellUpdate,
                editingCell = editingCell,
                onEditingCellChange = { editingCell = it }
            )
        }

        // Ligne de total et bonus
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.yahtzee_upper_total),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.yahtzee_bonus),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp
                    )
                }
            }

            playerGrids.forEach { playerGrid ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = playerGrid.grid.calculateUpperSectionTotal().toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (playerGrid.grid.calculateUpperSectionTotal() >= 63) {
                            Text(
                                text = "+${playerGrid.grid.calculateUpperSectionBonus()}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Section inférieure
        Text(
            text = stringResource(R.string.yahtzee_lower_section),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        val lowerCells = allCategories.filter { it.category == "lower" }
        lowerCells.forEach { category ->
            GridRow(
                category = category,
                playerGrids = playerGrids,
                onCellUpdate = onCellUpdate,
                editingCell = editingCell,
                onEditingCellChange = { editingCell = it }
            )
        }

        // Ligne de total final
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.yahtzee_total),
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
                        text = (playerGrid.grid.calculateTotal() + playerGrid.grid.calculateUpperSectionBonus()).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun GridRow(
    category: com.scoretally.domain.model.GridCell,
    playerGrids: List<PlayerGrid>,
    onCellUpdate: (Long, String, Int?) -> Unit,
    editingCell: Pair<Long, String>?,
    onEditingCellChange: (Pair<Long, String>?) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Colonne du label
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(40.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = category.label,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp
            )
        }

        // Colonnes des joueurs
        playerGrids.forEach { playerGrid ->
            val cell = playerGrid.grid.cells.find { it.id == category.id }
            val matchPlayerId = playerGrid.playerScore.matchPlayer.id
            val isEditing = editingCell == Pair(matchPlayerId, category.id)

            YahtzeeCell(
                cell = cell,
                isEditing = isEditing,
                onStartEdit = {
                    if (cell?.isLocked == false) {
                        onEditingCellChange(Pair(matchPlayerId, category.id))
                    }
                },
                onValueChange = { value ->
                    onCellUpdate(matchPlayerId, category.id, value)
                    onEditingCellChange(null)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun YahtzeeCell(
    cell: com.scoretally.domain.model.GridCell?,
    isEditing: Boolean,
    onStartEdit: () -> Unit,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val view = LocalView.current

    // Fermer le clavier quand on ouvre une cellule avec valeur fixe
    LaunchedEffect(isEditing) {
        if (isEditing && cell?.fixedValue != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    Box(
        modifier = modifier
            .height(40.dp)
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
            // Cellule avec valeur fixe en mode édition -> afficher les boutons
            isEditing && cell?.fixedValue != null -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onValueChange(0) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "0",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Button(
                        onClick = { onValueChange(cell.fixedValue) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            cell.fixedValue.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            // Cellule sans valeur fixe en mode édition -> afficher EditText natif
            isEditing -> {
                AndroidView(
                    factory = { ctx ->
                        EditText(ctx).apply {
                            inputType = InputType.TYPE_CLASS_NUMBER
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
                    color = if (cell?.value != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
