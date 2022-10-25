package com.seno.game.di

import com.seno.game.data.diff_picture.DiffPictureImpl
import com.seno.game.data.diff_picture.DiffPictureRepository
import com.seno.game.data.hunminjeongeum.HunMinJeongEumImpl
import com.seno.game.data.hunminjeongeum.HunMinJeongEumRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindHunMinJeongEumRepository(hunMinJeongEumImpl: HunMinJeongEumImpl): HunMinJeongEumRepository

    @Binds
    abstract fun bindDiffPictureRepository(diffPictureImpl: DiffPictureImpl): DiffPictureRepository
}