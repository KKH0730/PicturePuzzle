package com.seno.game.domain.usecase.user

import com.seno.game.data.config.ConfigRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.extensions.catchError
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GameConfigUseCase @Inject constructor(
    private val configRepository: ConfigRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun reqGetSavedGameInfo(params: String): Flow<Result<SavedGameInfo>> {
        return flow<Result<SavedGameInfo>> {
            emit(Result.Success(configRepository.getSavedGameInfo(uid = params)))
        }.catchError(ioDispatcher)
    }

    suspend fun reqUpdateBackgroundVolume(uid: String, volume: String): Flow<Result<Float>> {
        return flow<Result<Float>> {
            emit(Result.Success(configRepository.updateBackgroundVolume(uid = uid, volume = volume).toFloat()))
        }.catchError(ioDispatcher)
    }

    suspend fun reqUpdateEffectVolume(uid: String, volume: String): Flow<Result<Float>> {
        return flow<Result<Float>> {
            emit(Result.Success(configRepository.updateEffectVolume(uid = uid, volume = volume).toFloat()))
        }.catchError(ioDispatcher)
    }

    suspend fun reqUpdateVibrationOnOff(uid: String, isVibrationOn: Boolean): Flow<Result<Boolean>> {
        return flow<Result<Boolean>> {
            emit(Result.Success(configRepository.updateVibrationOnOff(uid = uid, isVibrationOn = isVibrationOn)))
        }.catchError(ioDispatcher)
    }

    suspend fun reqUpdatePushOnOff(uid: String, isPushOn: Boolean): Flow<Result<Boolean>> {
        return flow<Result<Boolean>> {
            emit(Result.Success(configRepository.updatePushOnOff(uid = uid, isPushOn = isPushOn)))
        }.catchError(ioDispatcher)
    }

    suspend fun reqUpdateADOnOff(uid: String, isShowAD: Boolean): Flow<Result<Boolean>> {
        return flow<Result<Boolean>> {
            emit(Result.Success(configRepository.updateADOnOff(uid = uid, isShowAD = isShowAD)))
        }.catchError(ioDispatcher)
    }
}