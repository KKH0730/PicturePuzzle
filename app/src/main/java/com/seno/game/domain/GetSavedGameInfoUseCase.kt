package com.seno.game.domain

import com.seno.game.data.user.UserInfoRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

class GetSavedGameInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
): BaseUseCase<String, SavedGameInfo>(dispatcher = ioDispatcher) {

    override suspend fun execute(params: String): SavedGameInfo {
        return userInfoRepository.getSavedGameInfo(uid = params)
    }
}