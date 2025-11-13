package com.scoretally

import android.content.Context
import androidx.startup.Initializer
import com.scoretally.domain.repository.GameRepository
import com.scoretally.domain.usecase.InitializePredefinedGamesUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppInitializerEntryPoint {
    fun gameRepository(): GameRepository
}

class PredefinedGamesInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            AppInitializerEntryPoint::class.java
        )

        val gameRepository = entryPoint.gameRepository()
        val useCase = InitializePredefinedGamesUseCase(gameRepository)

        // Lancer l'initialisation en arrière-plan
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            useCase()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
