package com.seno.game.di.network

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GeneralGson

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class NetworkGson