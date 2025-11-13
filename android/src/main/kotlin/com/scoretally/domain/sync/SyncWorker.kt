package com.scoretally.domain.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.scoretally.domain.repository.FirestoreSyncRepository
import com.scoretally.domain.repository.GoogleAuthRepository
import com.scoretally.domain.repository.PreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestoreSyncRepository: FirestoreSyncRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val preferencesRepository: PreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Vérifier si l'utilisateur est connecté
            if (!googleAuthRepository.isSignedIn()) {
                return Result.success()
            }

            // Exécuter la synchronisation
            val syncResult = firestoreSyncRepository.syncAll()

            if (syncResult.isSuccess) {
                // Mettre à jour le timestamp de dernière sync
                preferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
