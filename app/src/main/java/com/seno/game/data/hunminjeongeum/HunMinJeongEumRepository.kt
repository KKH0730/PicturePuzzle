package com.seno.game.data.hunminjeongeum

import com.seno.game.data.network.model.HumMinJeongEumResponse

interface HunMinJeongEumRepository {
    suspend fun searchWord(input: String): HumMinJeongEumResponse
}