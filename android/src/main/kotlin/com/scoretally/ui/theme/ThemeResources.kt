package com.scoretally.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.scoretally.R
import com.scoretally.domain.model.AppTheme

/**
 * Ressources thématiques pour les images de fond
 *
 * Images requises pour chaque thème :
 * - Light: bg_games.png, bg_matches.png, bg_players.png
 * - Dark: bg_games_dark.png, bg_matches_dark.png, bg_players_dark.png
 * - Cartoon: bg_games_cartoon.png, bg_matches_cartoon.png, bg_players_cartoon.png
 */
data class ThemeResources(
    @DrawableRes val bgGames: Int,
    @DrawableRes val bgMatches: Int,
    @DrawableRes val bgPlayers: Int
)

private val LightThemeResources = ThemeResources(
    bgGames = R.drawable.bg_games,
    bgMatches = R.drawable.bg_matches,
    bgPlayers = R.drawable.bg_players
)

private val DarkThemeResources = ThemeResources(
    // TODO: Créer les images bg_*_dark.png
    bgGames = R.drawable.bg_games,  // Temporaire
    bgMatches = R.drawable.bg_matches,  // Temporaire
    bgPlayers = R.drawable.bg_players  // Temporaire
)

private val CartoonThemeResources = ThemeResources(
    // TODO: Créer les images bg_*_cartoon.png
    bgGames = R.drawable.bg_games,  // Temporaire
    bgMatches = R.drawable.bg_matches,  // Temporaire
    bgPlayers = R.drawable.bg_players  // Temporaire
)

val LocalThemeResources = staticCompositionLocalOf { LightThemeResources }

@Composable
@ReadOnlyComposable
fun getThemeResources(theme: AppTheme?, isDark: Boolean): ThemeResources {
    return when {
        theme == AppTheme.CARTOON -> CartoonThemeResources
        theme == AppTheme.DARK || (theme == AppTheme.SYSTEM && isDark) -> DarkThemeResources
        else -> LightThemeResources
    }
}
