package com.seno.game.domain.usecase.user

import com.seno.game.data.user.UserInfoRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.domain.BaseUseCase
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseUseCase<String, SavedGameInfo>(dispatcher = ioDispatcher) {

    override suspend fun execute(params: String): Flow<Result<SavedGameInfo>> {
        return flow { emit(Result.Success(userInfoRepository.getSavedGameInfo(uid = params))) }
    }

    suspend fun updateBackgroundVolume(uid: String, volume: String): Flow<Result<Float>> {
        return flow<Result<Float>> {
            emit(
                Result.Success(userInfoRepository.updateBackgroundVolume(
                    uid = uid,
                    volume = volume
                ).toFloat())
            )
        }
            .catch {
                Timber.e(it)
                emit(Result.Error(Exception(it)))
            }.flowOn(ioDispatcher)
    }

    suspend fun updateEffectVolume(uid: String, volume: String): Flow<Result<Float>> {
        return flow<Result<Float>> {
            emit(
                Result.Success(userInfoRepository.updateEffectVolume(
                    uid = uid,
                    volume = volume
                ).toFloat())
            )
        }
            .catch {
                Timber.e(it)
                emit(Result.Error(Exception(it)))
            }.flowOn(ioDispatcher)
    }
}