package com.seno.game.data.hunminjeongeum

import com.google.gson.JsonElement
import com.seno.game.data.network.ApiConstants.ApiKey.DICTIONARY_API_KEY
import com.seno.game.data.network.model.HumMinJeongEumResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class HunMinJeongEumImpl @Inject constructor(
    private val hunMinJeongEumService: HunMinJeongEumService,
) : HunMinJeongEumRepository {
    override suspend fun searchWord(input: String): HumMinJeongEumResponse {
        return hunMinJeongEumService.searchWord(
            key = DICTIONARY_API_KEY,
            q = input,
            reqType = "json",
        )
    }
}