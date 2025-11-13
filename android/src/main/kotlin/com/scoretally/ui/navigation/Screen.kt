package com.scoretally.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Games : Screen("games")
    object Players : Screen("players")
    object Matches : Screen("matches")
    object Stats : Screen("stats")
    object GameDetail : Screen("game/{gameId}") {
        fun createRoute(gameId: Long) = "game/$gameId"
    }
    object PlayerDetail : Screen("player/{playerId}") {
        fun createRoute(playerId: Long) = "player/$playerId"
    }
    object AddGame : Screen("add_game")
    object EditGame : Screen("edit_game/{gameId}") {
        fun createRoute(gameId: Long) = "edit_game/$gameId"
    }
    object AddPlayer : Screen("add_player")
    object EditPlayer : Screen("edit_player/{playerId}") {
        fun createRoute(playerId: Long) = "edit_player/$playerId"
    }
    object AddMatch : Screen("add_match")
    object QuickMatch : Screen("quick_match")
    object MatchDetail : Screen("match/{matchId}") {
        fun createRoute(matchId: Long) = "match/$matchId"
    }
    object Settings : Screen("settings")
}
