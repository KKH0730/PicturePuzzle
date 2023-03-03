package com.seno.game.domain.usecase.user

import com.seno.game.data.user.UserInfoRepository
import com.seno.game.di.coroutine.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
}