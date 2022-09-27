package com.seno.game.di.network

import com.google.gson.Gson
import com.seno.game.data.network.DefaultParamsInterceptor
import com.seno.game.util.TrustOkHttpClientUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val X_API_KEY = "x-api-key: tZXZmgGslf3U868aknFji2LT6p0DhgC49OG7yG0g"

private const val TIMEOUT_SECONDS = 45L
private const val BASE_URL = "https://gl07095aue.execute-api.ap-northeast-2.amazonaws.com/prod/"
private const val HUNMINJEONGEUM_BASE_URL = "https://stdict.korean.go.kr/api/"

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideDefaultParamsInterceptor(): DefaultParamsInterceptor = DefaultParamsInterceptor()

//    @Singleton
//    @Provides
//    fun provideOkHttpClient(
//        defaultParamsInterceptor: DefaultParamsInterceptor,
//    ) = OkHttpClient.Builder()
//        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//        .addNetworkInterceptor(defaultParamsInterceptor)
//        .addNetworkInterceptor(
//            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
//        ).build()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        defaultParamsInterceptor: DefaultParamsInterceptor,
    ): OkHttpClient {
        val builder = TrustOkHttpClientUtil.getUnsafeOkHttpClient()
        return builder.writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addNetworkInterceptor(defaultParamsInterceptor)
            .addNetworkInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            ).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(@NetworkGson gson: Gson, okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    @HunMinJeongEumRetrofit
    @Singleton
    @Provides
    fun provideHunMinJeongEumRetrofit(
        @NetworkGson gson: Gson,
        okHttpClient: OkHttpClient,
    ): Retrofit =
        createRetrofit(
            url = HUNMINJEONGEUM_BASE_URL,
            gson = gson,
            okHttpClient = okHttpClient
        )

    private fun createRetrofit(url: String, gson: Gson, okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
}
