package com.scoretally.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.scoretally.data.local.dao.GameDao
import com.scoretally.data.local.dao.MatchDao
import com.scoretally.data.local.dao.PlayerDao
import com.scoretally.data.local.entity.toDomain
import com.scoretally.data.local.entity.toEntity
import com.scoretally.data.remote.dto.*
import com.scoretally.domain.model.Game
import com.scoretally.domain.model.Match
import com.scoretally.domain.model.Player
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val googleAuthRepository: GoogleAuthRepository,
    private val playerDao: PlayerDao,
    private val gameDao: GameDao,
    private val matchDao: MatchDao
) {

    private fun getUserCollection(collection: String): String {
        val userId = googleAuthRepository.authState.value.userId
            ?: throw IllegalStateException("User must be signed in to sync")
        return "users/$userId/$collection"
    }

    // ==================== PLAYERS ====================

    suspend fun syncPlayers(): Result<Unit> {
        return try {
            uploadPlayers()
            downloadPlayers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadPlayers() {
        val localPlayers = playerDao.getAllPlayers().first()
        val batch = firestore.batch()

        localPlayers.forEach { player ->
            val docRef = firestore.collection(getUserCollection("players"))
                .document(player.syncId)
            batch.set(docRef, player.toDomain().toDto())
        }

        batch.commit().await()
    }

    private suspend fun downloadPlayers() {
        val snapshot = firestore.collection(getUserCollection("players"))
            .get()
            .await()

        snapshot.documents.forEach { doc ->
            val remotePlayer = doc.toPlayerDto() ?: return@forEach
            val localPlayer = playerDao.getPlayerBySyncId(remotePlayer.syncId)

            when {
                // Nouveau joueur distant
                localPlayer == null && !remotePlayer.isDeleted -> {
                    playerDao.insertPlayer(remotePlayer.toDomain().toEntity())
                }
                // Conflit : résolution last-write-wins
                localPlayer != null -> {
                    if (remotePlayer.lastModifiedAt > localPlayer.lastModifiedAt) {
                        if (remotePlayer.isDeleted) {
                            playerDao.deletePlayer(localPlayer)
                        } else {
                            playerDao.updatePlayer(
                                remotePlayer.toDomain(localId = localPlayer.id).toEntity()
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== GAMES ====================

    suspend fun syncGames(): Result<Unit> {
        return try {
            uploadGames()
            downloadGames()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadGames() {
        val localGames = gameDao.getAllGames().first()
        val batch = firestore.batch()

        localGames.forEach { game ->
            val docRef = firestore.collection(getUserCollection("games"))
                .document(game.syncId)
            batch.set(docRef, game.toDomain().toDto())
        }

        batch.commit().await()
    }

    private suspend fun downloadGames() {
        val snapshot = firestore.collection(getUserCollection("games"))
            .get()
            .await()

        snapshot.documents.forEach { doc ->
            val remoteGame = doc.toGameDto() ?: return@forEach
            val localGame = gameDao.getGameBySyncId(remoteGame.syncId)

            when {
                localGame == null && !remoteGame.isDeleted -> {
                    gameDao.insertGame(remoteGame.toDomain().toEntity())
                }
                localGame != null -> {
                    if (remoteGame.lastModifiedAt > localGame.lastModifiedAt) {
                        if (remoteGame.isDeleted) {
                            gameDao.deleteGame(localGame)
                        } else {
                            gameDao.updateGame(
                                remoteGame.toDomain(localId = localGame.id).toEntity()
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== MATCHES ====================

    suspend fun syncMatches(): Result<Unit> {
        return try {
            uploadMatches()
            downloadMatches()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadMatches() {
        val localMatches = matchDao.getAllMatches().first()
        val batch = firestore.batch()

        localMatches.forEach { matchEntity ->
            val match = matchEntity.toDomain()
            val game = gameDao.getGameById(match.gameId)

            if (game != null) {
                val docRef = firestore.collection(getUserCollection("matches"))
                    .document(match.syncId)
                batch.set(docRef, match.toDto(gameSyncId = game.syncId))
            }
        }

        batch.commit().await()
    }

    private suspend fun downloadMatches() {
        val snapshot = firestore.collection(getUserCollection("matches"))
            .get()
            .await()

        snapshot.documents.forEach { doc ->
            val remoteMatch = doc.toMatchDto() ?: return@forEach
            val localMatch = matchDao.getMatchBySyncId(remoteMatch.syncId)

            // Résoudre la référence au jeu local via syncId
            val localGame = gameDao.getGameBySyncId(remoteMatch.gameId)
            if (localGame == null && !remoteMatch.isDeleted) {
                // Le jeu n'existe pas localement, skip pour l'instant
                return@forEach
            }

            val localGameId = localGame?.id ?: 0

            when {
                localMatch == null && !remoteMatch.isDeleted -> {
                    matchDao.insertMatch(
                        remoteMatch.toDomain(localGameId = localGameId).toEntity()
                    )
                }
                localMatch != null -> {
                    if (remoteMatch.lastModifiedAt > localMatch.lastModifiedAt) {
                        if (remoteMatch.isDeleted) {
                            matchDao.deleteMatch(localMatch)
                        } else {
                            matchDao.updateMatch(
                                remoteMatch.toDomain(
                                    localId = localMatch.id,
                                    localGameId = localGameId
                                ).toEntity()
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== FULL SYNC ====================

    suspend fun syncAll(): Result<Unit> {
        return try {
            // Ordre important : Players et Games d'abord (pas de dépendances)
            // Puis Matches (dépend de Games)
            syncPlayers()
            syncGames()
            syncMatches()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
