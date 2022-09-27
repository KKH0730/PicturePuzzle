package com.seno.game.data.hunminjeongeum

import com.google.gson.JsonElement
import com.seno.game.data.network.model.HumMinJeongEumResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

interface HunMinJeongEumRepository {
    suspend fun searchWord(input: String): HumMinJeongEumResponse
}