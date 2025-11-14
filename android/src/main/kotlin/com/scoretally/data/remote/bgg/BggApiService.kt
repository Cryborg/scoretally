package com.scoretally.data.remote.bgg

import com.scoretally.data.remote.bgg.dto.BggDetailsResponse
import com.scoretally.data.remote.bgg.dto.BggSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BggApiService {
    @GET("xmlapi2/search")
    suspend fun search(
        @Query("query") query: String,
        @Query("type") type: String = "boardgame"
    ): BggSearchResponse

    @GET("xmlapi2/thing")
    suspend fun getDetails(
        @Query("id") id: String,
        @Query("stats") stats: Int = 1
    ): BggDetailsResponse
}
