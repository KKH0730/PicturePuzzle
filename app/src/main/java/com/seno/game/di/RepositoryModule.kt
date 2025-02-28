package com.seno.game.di

import com.seno.game.data.network.repository.ConfigImpl
import com.seno.game.data.network.repository.ConfigRepository
import com.seno.game.data.network.repository.DiffPictureImpl
import com.seno.game.data.network.repository.DiffPictureRepository
import com.seno.game.data.user.UserInfoImpl
import com.seno.game.data.user.UserInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDiffPictureRepository(diffPictureImpl: DiffPictureImpl): DiffPictureRepository

    @Binds
    abstract fun bindUserInfoRepository(userInfoImpl: UserInfoImpl): UserInfoRepository

    @Binds
    abstract fun bindConfigRepository(configImpl: ConfigImpl): ConfigRepository
}