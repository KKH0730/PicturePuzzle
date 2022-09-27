package com.seno.game.data.hunminjeongeum

import com.seno.game.data.network.model.HumMinJeongEumResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HunMinJeongEumService {

    @GET("search.do")
    suspend fun searchWord(
        @Query("key") key: String,
        @Query("q") q: String,
        @Query("req_type") reqType: String
    ): HumMinJeongEumResponse
}