package com.seno.game.domain.usecase.user

import com.seno.game.data.user.UserInfoRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.model.Result
import com.seno.game.model.UserInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun reqGetSavedGameInfo(uid: String) : Flow<Result<UserInfo>> {
        return flow {

        }
    }

}