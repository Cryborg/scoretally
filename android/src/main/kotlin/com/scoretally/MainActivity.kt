package com.scoretally

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scoretally.domain.repository.PreferencesRepository
import com.scoretally.domain.usecase.InitializePredefinedGamesUseCase
import com.scoretally.ui.components.BottomNavBar
import com.scoretally.ui.navigation.NavGraph
import com.scoretally.ui.theme.AppThemeProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var initializePredefinedGamesUseCase: InitializePredefinedGamesUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Log le démarrage de l'app
        FirebaseCrashlytics.getInstance().log("MainActivity: App started")

        // Initialiser les jeux prédéfinis avec la langue correcte
        // (le contexte a déjà la bonne locale grâce à AppCompatDelegate.setApplicationLocales)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            initializePredefinedGamesUseCase()
        }

        setContent {
            AppThemeProvider(preferencesRepository = preferencesRepository) {
                val navController = rememberNavController()
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != "settings" && currentRoute != "tools") {
                            BottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
