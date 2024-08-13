package com.seno.game.data.config

import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    suspend fun getSavedGameInfo(uid: String): Flow<Result<SavedGameInfo>>

    suspend fun updateBackgroundVolume(uid: String, volume: String) : Flow<Result<Float>>

    suspend fun updateEffectVolume(uid: String, volume: String) : Flow<Result<Float>>

    suspend fun updateVibrationOnOff(uid: String, isVibrationOn: Boolean) : Flow<Result<Boolean>>

    suspend fun updatePushOnOff(uid: String, isPushOn: Boolean) : Flow<Result<Boolean>>

    suspend fun updateADOnOff(uid: String, isShowAD: Boolean) : Flow<Result<Boolean>>

    suspend fun updateNickname(uid: String, nickname: String) : Flow<Result<String>>
}