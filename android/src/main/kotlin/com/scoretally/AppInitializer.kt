package com.scoretally

import android.content.Context
import androidx.startup.Initializer
import com.scoretally.domain.repository.GameRepository
import com.scoretally.domain.usecase.InitializePredefinedGamesUseCase
import com.scoretally.util.ResourceProvider
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
    fun resourceProvider(): ResourceProvider
}

class PredefinedGamesInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            AppInitializerEntryPoint::class.java
        )

        val gameRepository = entryPoint.gameRepository()
        val resourceProvider = entryPoint.resourceProvider()
        val useCase = InitializePredefinedGamesUseCase(gameRepository, resourceProvider)

        // Lancer l'initialisation en arrière-plan
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            useCase()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
