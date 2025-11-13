package com.scoretally.ui.players

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
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
import com.scoretally.domain.model.Player
import com.scoretally.ui.components.EmptyState
import com.scoretally.ui.components.toComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    onNavigateToAddPlayer: () -> Unit,
    onNavigateToPlayerDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTools: () -> Unit,
    viewModel: PlayersViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    var playerToDelete by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.players_title)) },
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
            FloatingActionButton(onClick = onNavigateToAddPlayer) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image with transparency
            Image(
                painter = painterResource(R.drawable.bg_players),
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
            if (players.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.players_empty),
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
                    items(players) { player ->
                        PlayerItem(
                            player = player,
                            onClick = { onNavigateToPlayerDetail(player.id) },
                            onDelete = { playerToDelete = player }
                        )
                    }
                }
            }
        }
    }

    playerToDelete?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { Text(stringResource(R.string.delete_player_title)) },
            text = { Text(stringResource(R.string.delete_player_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlayer(player.id)
                        playerToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun PlayerItem(
    player: Player,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .background(
                        color = player.preferredColor.toComposeColor(),
                        shape = MaterialTheme.shapes.small
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
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
