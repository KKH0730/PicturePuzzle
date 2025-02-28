package com.seno.game.domain.usecase.user

import com.seno.game.data.network.repository.ConfigRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.extensions.catchError
import com.seno.game.data.model.Result
import com.seno.game.data.model.SavedGameInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameConfigUseCase @Inject constructor(
    private val configRepository: ConfigRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun reqGetSavedGameInfo(params: String): Flow<Result<SavedGameInfo>> {
        return configRepository.getSavedGameInfo(uid = params).catchError(dispatcher = ioDispatcher)
    }

    suspend fun reqUpdateBackgroundVolume(uid: String, volume: String): Flow<Result<Float>> {
        return configRepository.updateBackgroundVolume(uid = uid, volume = volume).catchError(dispatcher = ioDispatcher)
    }

    suspend fun reqUpdateEffectVolume(uid: String, volume: String): Flow<Result<Float>> {
        return configRepository.updateEffectVolume(uid = uid, volume = volume).catchError(dispatcher = ioDispatcher)
    }

    suspend fun reqUpdateVibrationOnOff(uid: String, isVibrationOn: Boolean): Flow<Result<Boolean>> {
        return configRepository.updateVibrationOnOff(uid = uid, isVibrationOn = isVibrationOn).catchError(dispatcher = ioDispatcher)
    }

    suspend fun reqUpdatePushOnOff(uid: String, isPushOn: Boolean): Flow<Result<Boolean>> {
        return configRepository.updatePushOnOff(uid = uid, isPushOn = isPushOn).catchError(dispatcher = ioDispatcher)
    }

    suspend fun reqUpdateADOnOff(uid: String, isShowAD: Boolean): Flow<Result<Boolean>> {
        return configRepository.updateADOnOff(uid = uid, isShowAD = isShowAD).catchError(dispatcher = ioDispatcher)
    }
}