package com.seno.game.data.config

import com.seno.game.model.SavedGameInfo

interface ConfigRepository {
    suspend fun getSavedGameInfo(uid: String): SavedGameInfo

    suspend fun updateBackgroundVolume(uid: String, volume: String) : String

    suspend fun updateEffectVolume(uid: String, volume: String) : String

    suspend fun updateVibrationOnOff(uid: String, isVibrationOn: Boolean) : Boolean

    suspend fun updatePushOnOff(uid: String, isPushOn: Boolean) : Boolean

    suspend fun updateADOnOff(uid: String, isShowAD: Boolean) : Boolean
}