package com.scoretally.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scoretally.R
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scoretally.ui.games.AddGameScreen
import com.scoretally.ui.games.GamesScreen
import com.scoretally.ui.matches.AddMatchScreen
import com.scoretally.ui.matches.MatchDetailScreen
import com.scoretally.ui.matches.MatchesScreen
import com.scoretally.ui.players.AddPlayerScreen
import com.scoretally.ui.players.PlayersScreen
import com.scoretally.ui.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Matches.route,
        modifier = modifier
    ) {
        composable(Screen.Matches.route) {
            MatchesScreen(
                onNavigateToAddMatch = {
                    navController.navigate(Screen.AddMatch.route)
                },
                onNavigateToMatchDetail = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                }
            )
        }

        composable(Screen.Games.route) {
            GamesScreen(
                onNavigateToAddGame = {
                    navController.navigate(Screen.AddGame.route)
                },
                onNavigateToGameDetail = { gameId ->
                    navController.navigate(Screen.GameDetail.createRoute(gameId))
                }
            )
        }

        composable(Screen.Players.route) {
            PlayersScreen(
                onNavigateToAddPlayer = {
                    navController.navigate(Screen.AddPlayer.route)
                },
                onNavigateToPlayerDetail = { playerId ->
                    navController.navigate(Screen.PlayerDetail.createRoute(playerId))
                }
            )
        }

        // Formulaires d'ajout
        composable(Screen.AddGame.route) {
            AddGameScreen(
                onGameSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddPlayer.route) {
            AddPlayerScreen(
                onPlayerSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddMatch.route) {
            AddMatchScreen(
                onMatchSaved = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId)) {
                        popUpTo(Screen.Matches.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.GameDetail.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) {
            PlaceholderScreen(
                title = stringResource(R.string.game_detail),
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PlayerDetail.route,
            arguments = listOf(navArgument("playerId") { type = NavType.LongType })
        ) {
            PlaceholderScreen(
                title = stringResource(R.string.player_detail),
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MatchDetail.route,
            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
        ) {
            MatchDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    stringResource(R.string.placeholder_message),
                    style = MaterialTheme.typography.titleLarge
                )
                Button(onClick = onBack) {
                    Text(stringResource(R.string.back))
                }
            }
        }
    }
}
