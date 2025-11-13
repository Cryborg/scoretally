package com.scoretally.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.scoretally.domain.model.AppLanguage
import com.scoretally.domain.model.AppTheme
import com.scoretally.domain.model.UserPreferences
import com.scoretally.domain.repository.PreferencesRepository
import java.util.Locale

@Composable
fun AppThemeProvider(
    preferencesRepository: PreferencesRepository,
    content: @Composable () -> Unit
) {
    val userPreferences by preferencesRepository.userPreferences.collectAsState(
        initial = UserPreferences()
    )
    val systemInDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    // Track previous language to detect changes
    var previousLanguage by remember { mutableStateOf<AppLanguage?>(null) }

    // Apply language preference and recreate activity on change
    LaunchedEffect(userPreferences.language) {
        if (previousLanguage != null && previousLanguage != userPreferences.language) {
            // Language changed, apply and recreate
            applyLanguage(userPreferences.language)
            (context as? Activity)?.recreate()
        } else {
            // First load, just apply without recreating
            applyLanguage(userPreferences.language)
            previousLanguage = userPreferences.language
        }
    }

    // Determine dark theme based on preference
    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> systemInDarkTheme
    }

    // Apply theme via AppCompatDelegate (affects system UI)
    LaunchedEffect(userPreferences.theme) {
        val nightMode = when (userPreferences.theme) {
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    ScoreTallyTheme(
        darkTheme = darkTheme,
        content = content
    )
}

private fun applyLanguage(language: AppLanguage) {
    if (language == AppLanguage.SYSTEM) {
        // Reset to system default
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            // For older APIs, let the system handle it
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        }
    } else {
        // Set specific locale
        val locale = Locale(language.code)
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
