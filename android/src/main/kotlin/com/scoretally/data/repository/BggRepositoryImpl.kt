package com.scoretally.data.repository

import android.content.Context
import com.scoretally.data.remote.bgg.BggApiService
import com.scoretally.data.remote.bgg.dto.BggSearchItem
import com.scoretally.data.remote.bgg.mapper.BggMapper
import com.scoretally.domain.model.Game
import com.scoretally.domain.repository.BggRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

class BggRepositoryImpl @Inject constructor(
    private val bggApiService: BggApiService,
    private val okHttpClient: OkHttpClient,
    @ApplicationContext private val context: Context
) : BggRepository {

    override suspend fun searchGames(query: String): Result<List<BggSearchItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bggApiService.search(query)
                Result.success(response.items ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getGameDetails(bggId: String): Result<Game> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bggApiService.getDetails(bggId)
                val bggItem = response.items?.firstOrNull()
                    ?: return@withContext Result.failure(Exception("Game not found"))

                val imageUrl = bggItem.image
                val localImageUri = if (!imageUrl.isNullOrEmpty()) {
                    downloadImage(imageUrl)
                } else {
                    null
                }

                val game = BggMapper.mapToGame(bggItem, localImageUri)
                Result.success(game)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun downloadImage(imageUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(imageUrl)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (!response.isSuccessful) return@withContext null

                val imageBytes = response.body?.bytes() ?: return@withContext null

                val imagesDir = File(context.filesDir, "game_images")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                val imageFile = File(imagesDir, "${UUID.randomUUID()}.jpg")
                FileOutputStream(imageFile).use { output ->
                    output.write(imageBytes)
                }

                imageFile.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }
}
