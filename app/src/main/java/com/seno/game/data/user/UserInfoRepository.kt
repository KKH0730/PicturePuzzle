package com.seno.game.data.user

import com.seno.game.model.SavedGameInfo

interface UserInfoRepository {
    suspend fun getSavedGameInfo(uid: String): SavedGameInfo

    suspend fun updateBackgroundVolume(uid: String, volume: String) : String

    suspend fun updateEffectVolume(uid: String, volume: String) : String
}