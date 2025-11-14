package com.scoretally.domain.repository

import com.scoretally.data.remote.bgg.dto.BggSearchItem
import com.scoretally.domain.model.Game

interface BggRepository {
    suspend fun searchGames(query: String): Result<List<BggSearchItem>>
    suspend fun getGameDetails(bggId: String): Result<Game>
}
