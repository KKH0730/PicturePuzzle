package com.seno.game.data.user

import com.seno.game.model.SavedGameInfo

interface UserInfoRepository {
    suspend fun getSavedGameInfo(uid: String): SavedGameInfo
}