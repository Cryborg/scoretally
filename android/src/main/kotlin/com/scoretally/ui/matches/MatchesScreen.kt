package com.scoretally.ui.matches

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.domain.model.MatchListItem
import com.scoretally.ui.components.EmptyState
import com.scoretally.ui.components.ExpandableFAB
import com.scoretally.ui.components.FABMenuItem
import com.scoretally.ui.theme.LocalThemeResources
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onNavigateToAddMatch: () -> Unit,
    onNavigateToQuickMatch: () -> Unit,
    onNavigateToMatchDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTools: () -> Unit,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    var matchToDelete by remember { mutableStateOf<MatchListItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.matches_title)) },
                actions = {
                    IconButton(onClick = onNavigateToTools) {
                        Icon(Icons.Default.Build, contentDescription = stringResource(R.string.tools_title))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                }
            )
        },
        floatingActionButton = {
            ExpandableFAB(
                items = listOf(
                    FABMenuItem(
                        label = stringResource(R.string.match_create_quick),
                        icon = Icons.Default.PlayArrow,
                        onClick = onNavigateToQuickMatch
                    ),
                    FABMenuItem(
                        label = stringResource(R.string.match_create_full),
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = onNavigateToAddMatch
                    )
                )
            )
        }
    ) { padding ->
        val themeResources = LocalThemeResources.current

        Box(modifier = Modifier.fillMaxSize()) {
            // Background image with transparency
            Image(
                painter = painterResource(themeResources.bgMatches),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.3f),
                contentScale = ContentScale.Crop
            )

            // Vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                            ),
                            radius = 1200f
                        )
                    )
            )

            // Content
            if (matches.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.matches_empty),
                    modifier = Modifier.padding(padding)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(matches) { matchListItem ->
                        MatchItem(
                            matchListItem = matchListItem,
                            onClick = { onNavigateToMatchDetail(matchListItem.match.id) },
                            onDelete = { matchToDelete = matchListItem }
                        )
                    }
                }
            }
        }
    }

    matchToDelete?.let { matchItem ->
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = { Text(stringResource(R.string.delete_match_title)) },
            text = { Text(stringResource(R.string.delete_match_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMatch(matchItem.match.id)
                        matchToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun MatchItem(
    matchListItem: MatchListItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val match = matchListItem.match

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = matchListItem.gameName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (match.isCompleted)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = stringResource(
                                if (match.isCompleted) R.string.match_status_completed
                                else R.string.match_status_in_progress
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = match.date.format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (matchListItem.playerNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = matchListItem.playerNames.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            match.duration?.let { duration ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.match_duration_label, duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
                if (match.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = match.notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
