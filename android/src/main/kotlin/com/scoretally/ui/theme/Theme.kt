package com.scoretally.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.scoretally.domain.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBlue,
    onPrimary = Color.White,
    primaryContainer = SoftCyan,
    onPrimaryContainer = Color(0xFF004D66),
    secondary = SoftLavender,
    onSecondary = Color.White,
    secondaryContainer = SoftMint,
    onSecondaryContainer = Color(0xFF2D5A4A),
    tertiary = SoftRose,
    onTertiary = Color.White,
    tertiaryContainer = SoftPeach,
    onTertiaryContainer = Color(0xFF664033),
    background = LightBg,
    onBackground = Color(0xFF1A1C1E),
    surface = LightSurface,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF44474E),
    outline = SoftSage,
    outlineVariant = Color(0xFFCAC4D0)
)

private val CartoonColorScheme = lightColorScheme(
    primary = CartoonOrange,
    onPrimary = CartoonSurfaceLight,
    primaryContainer = CartoonYellow,
    onPrimaryContainer = CartoonOrangeDark,
    secondary = CartoonCyan,
    onSecondary = CartoonBlueDark,
    secondaryContainer = CartoonBlue,
    onSecondaryContainer = CartoonBlueDark,
    tertiary = CartoonPink,
    onTertiary = CartoonSurfaceLight,
    tertiaryContainer = CartoonPurple,
    onTertiaryContainer = CartoonPurpleDark,
    background = CartoonBgLight,
    onBackground = CartoonOrangeDark,
    surface = CartoonSurfaceLight,
    onSurface = CartoonOrangeDark,
    surfaceVariant = CartoonBgContainer,
    onSurfaceVariant = CartoonOrangeDark,
    outline = CartoonCyan,
    outlineVariant = CartoonBlue
)

@Composable
fun ScoreTallyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    theme: AppTheme? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        theme == AppTheme.CARTOON -> CartoonColorScheme

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && theme != AppTheme.CARTOON -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val isLightTheme = theme == AppTheme.CARTOON || !darkTheme
    val themeResources = getThemeResources(theme, darkTheme)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightTheme
        }
    }

    CompositionLocalProvider(LocalThemeResources provides themeResources) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
