package com.seno.game.di

import com.seno.game.data.hunminjeongeum.HunMinJeongEumService
import com.seno.game.di.network.HunMinJeongEumRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Singleton
    @Provides
    fun provideHunMinJeongEumService(@HunMinJeongEumRetrofit retrofit: Retrofit): HunMinJeongEumService = retrofit.create()
}