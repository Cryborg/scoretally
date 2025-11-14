package com.scoretally.ui.navigation

sealed interface Screen {
    val route: String

    data object Games : Screen {
        override val route = "games"
    }

    data object Players : Screen {
        override val route = "players"
    }

    data object Matches : Screen {
        override val route = "matches"
    }

    data object Tools : Screen {
        override val route = "tools"
    }

    data object AddGame : Screen {
        override val route = "add_game"
    }

    data object SearchBgg : Screen {
        override val route = "search_bgg"
    }

    data object EditGame : Screen {
        override val route = "edit_game/{gameId}"
        fun createRoute(gameId: Long) = "edit_game/$gameId"
    }

    data object AddPlayer : Screen {
        override val route = "add_player"
    }

    data object EditPlayer : Screen {
        override val route = "edit_player/{playerId}"
        fun createRoute(playerId: Long) = "edit_player/$playerId"
    }

    data object AddMatch : Screen {
        override val route = "add_match"
    }

    data object QuickMatch : Screen {
        override val route = "quick_match"
    }

    data object MatchDetail : Screen {
        override val route = "match/{matchId}"
        fun createRoute(matchId: Long) = "match/$matchId"
    }

    data object Settings : Screen {
        override val route = "settings"
    }
}
