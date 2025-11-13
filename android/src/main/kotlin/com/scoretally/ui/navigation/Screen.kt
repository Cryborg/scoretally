package com.scoretally.ui.navigation

sealed class Screen(val route: String) {
    object Games : Screen("games")
    object Players : Screen("players")
    object Matches : Screen("matches")
    object GameDetail : Screen("game/{gameId}") {
        fun createRoute(gameId: Long) = "game/$gameId"
    }
    object PlayerDetail : Screen("player/{playerId}") {
        fun createRoute(playerId: Long) = "player/$playerId"
    }
    object AddGame : Screen("add_game")
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
