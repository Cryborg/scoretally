package com.scoretally.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoretally.R
import com.scoretally.domain.model.AppLanguage
import com.scoretally.domain.model.AppTheme
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val signInError by viewModel.signInError.collectAsStateWithLifecycle()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }

    LaunchedEffect(signInError) {
        signInError?.let {
            // L'erreur sera affichée dans le Snackbar ou dans l'UI
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LanguageSelector(
                currentLanguage = userPreferences.language,
                onLanguageSelected = viewModel::updateLanguage
            )

            ThemeSelector(
                currentTheme = userPreferences.theme,
                onThemeSelected = viewModel::updateTheme
            )

            GoogleSignInSection(
                authState = authState,
                onSignInClick = { signInLauncher.launch(viewModel.getSignInIntent()) },
                onSignOutClick = viewModel::signOut
            )

            if (authState.isSignedIn) {
                SyncSection(
                    autoSyncEnabled = userPreferences.autoSyncEnabled,
                    lastSyncTimestamp = userPreferences.lastSyncTimestamp,
                    onAutoSyncToggle = viewModel::updateAutoSync,
                    onSyncNowClick = viewModel::triggerManualSync
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = getLanguageName(currentLanguage),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppLanguage.entries.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(getLanguageName(language)) },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false
                        },
                        leadingIcon = {
                            if (language == currentLanguage) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            } else {
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.settings_theme),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = getThemeName(currentTheme),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppTheme.entries.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(getThemeName(theme)) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        },
                        leadingIcon = {
                            if (theme == currentTheme) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            } else {
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun getLanguageName(language: AppLanguage): String {
    return when (language) {
        AppLanguage.SYSTEM -> stringResource(R.string.settings_language_system)
        AppLanguage.ENGLISH -> stringResource(R.string.settings_language_english)
        AppLanguage.FRENCH -> stringResource(R.string.settings_language_french)
        AppLanguage.SPANISH -> stringResource(R.string.settings_language_spanish)
        AppLanguage.GERMAN -> stringResource(R.string.settings_language_german)
        AppLanguage.ITALIAN -> stringResource(R.string.settings_language_italian)
    }
}

@Composable
fun getThemeName(theme: AppTheme): String {
    return when (theme) {
        AppTheme.SYSTEM -> stringResource(R.string.settings_theme_system)
        AppTheme.LIGHT -> stringResource(R.string.settings_theme_light)
        AppTheme.DARK -> stringResource(R.string.settings_theme_dark)
    }
}

@Composable
fun GoogleSignInSection(
    authState: com.scoretally.domain.model.AuthState,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column {
        Text(
            text = "Google Sync",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (authState.isSignedIn) {
                    // Utilisateur connecté
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = authState.displayName ?: authState.userEmail ?: "User",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (authState.displayName != null && authState.userEmail != null) {
                            Text(
                                text = authState.userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = onSignOutClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Se déconnecter")
                    }
                } else {
                    // Utilisateur non connecté
                    Text(
                        text = "Synchronisez vos données entre plusieurs appareils",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Se connecter avec Google")
                    }
                }
            }
        }
    }
}

@Composable
fun SyncSection(
    autoSyncEnabled: Boolean,
    lastSyncTimestamp: Long,
    onAutoSyncToggle: (Boolean) -> Unit,
    onSyncNowClick: () -> Unit
) {
    Column {
        Text(
            text = "Synchronisation",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Toggle Auto-Sync
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Synchronisation automatique",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Synchronise toutes les heures",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoSyncEnabled,
                        onCheckedChange = onAutoSyncToggle
                    )
                }

                HorizontalDivider()

                // Dernière sync
                if (lastSyncTimestamp > 0) {
                    val timeSinceSync = formatTimeSinceSync(lastSyncTimestamp)
                    Text(
                        text = "Dernière sync : $timeSinceSync",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Aucune synchronisation effectuée",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bouton Sync Now
                Button(
                    onClick = onSyncNowClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = autoSyncEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Synchroniser maintenant")
                }
            }
        }
    }
}

@Composable
fun formatTimeSinceSync(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diffMillis = abs(now - timestamp)

    val minutes = diffMillis / (1000 * 60)
    val hours = diffMillis / (1000 * 60 * 60)
    val days = diffMillis / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "à l'instant"
        minutes < 60 -> "il y a ${minutes}min"
        hours < 24 -> "il y a ${hours}h"
        days == 1L -> "il y a 1 jour"
        else -> "il y a ${days} jours"
    }
}
