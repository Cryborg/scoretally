package com.scoretally.domain.repository

import com.scoretally.domain.model.AppLanguage
import com.scoretally.domain.model.AppTheme
import com.scoretally.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateLanguage(language: AppLanguage)
    suspend fun updateTheme(theme: AppTheme)
}
