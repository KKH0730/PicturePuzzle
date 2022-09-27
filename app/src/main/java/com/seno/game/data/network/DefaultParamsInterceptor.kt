package com.seno.game.data.network

import okhttp3.Interceptor
import okhttp3.Response

class DefaultParamsInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        getDefaultHeader().forEach { (key, value) ->
            builder.addHeader(key, value)
        }

        return chain.proceed(builder.build())
    }

    /**
     * Rest API 에서 사용하는 default headers
     */
    private fun getDefaultHeader(): Map<String, String> = mutableMapOf(
        "x-api-key" to ApiConstants.Header.X_API_KEY,
    )
}