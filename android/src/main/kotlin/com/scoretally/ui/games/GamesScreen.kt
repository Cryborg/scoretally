package com.scoretally.ui.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.domain.model.Game
import com.scoretally.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onNavigateToAddGame: () -> Unit,
    onNavigateToEditGame: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTools: () -> Unit,
    viewModel: GamesViewModel = hiltViewModel()
) {
    val games by viewModel.games.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.games_title)) },
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
            FloatingActionButton(onClick = onNavigateToAddGame) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image with transparency
            Image(
                painter = painterResource(R.drawable.bg_games),
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
            if (games.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.games_empty),
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
                    items(games) { game ->
                        GameItem(
                            game = game,
                            onClick = {
                                if (!game.isPredefined) {
                                    onNavigateToEditGame(game.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameItem(
    game: Game,
    onClick: () -> Unit
) {
    val isClickable = !game.isPredefined && !game.isComingSoon
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable, onClick = onClick),
        colors = if (game.isComingSoon) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) else CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (game.isComingSoon) 0.6f else 1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (game.isComingSoon) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Bientôt",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (game.isPredefined) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Jeu par défaut",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.game_players_range, game.minPlayers, game.maxPlayers, game.averageDuration),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (game.category.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (game.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
