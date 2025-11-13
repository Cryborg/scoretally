package com.scoretally.ui.matches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.domain.model.Match
import com.scoretally.ui.components.EmptyState
import com.scoretally.ui.components.ExpandableFAB
import com.scoretally.ui.components.FABMenuItem
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onNavigateToAddMatch: () -> Unit,
    onNavigateToQuickMatch: () -> Unit,
    onNavigateToMatchDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val matches by viewModel.matches.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.matches_title)) },
                actions = {
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
                        icon = Icons.Default.List,
                        onClick = onNavigateToAddMatch
                    )
                )
            )
        }
    ) { padding ->
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
                items(matches) { match ->
                    MatchItem(
                        match = match,
                        onClick = { onNavigateToMatchDetail(match.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MatchItem(
    match: Match,
    onClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.match_game_label, match.gameId),
                    style = MaterialTheme.typography.titleMedium
                )
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
    }
}
