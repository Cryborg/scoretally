package com.scoretally.ui.settings

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.AppLanguage
import com.scoretally.domain.model.AppTheme
import com.scoretally.domain.model.AuthState
import com.scoretally.domain.model.UserPreferences
import com.scoretally.domain.repository.GoogleAuthRepository
import com.scoretally.domain.repository.PreferencesRepository
import com.scoretally.domain.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val authState: StateFlow<AuthState> = googleAuthRepository.authState

    private val _signInError = MutableStateFlow<String?>(null)
    val signInError: StateFlow<String?> = _signInError.asStateFlow()

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository.updateLanguage(language)
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }

    fun getSignInIntent(): Intent {
        return googleAuthRepository.getSignInIntent()
    }

    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            val wasSignedInBefore = userPreferences.value.lastSyncTimestamp > 0
            val result = googleAuthRepository.handleSignInResult(data)
            if (result.isFailure) {
                _signInError.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            } else if (!wasSignedInBefore) {
                // Première connexion : déclencher la sync automatiquement
                syncManager.triggerManualSync()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuthRepository.signOut()
        }
    }

    fun clearSignInError() {
        _signInError.value = null
    }

    fun updateAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAutoSyncEnabled(enabled)
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            syncManager.triggerManualSync()
        }
    }
}
