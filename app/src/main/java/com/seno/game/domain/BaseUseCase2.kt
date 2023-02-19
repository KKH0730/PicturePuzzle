package com.seno.game.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import com.seno.game.model.Result

abstract class BaseUseCase2<P, R>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(params: P): Result<R> {
        return try {
            withContext(dispatcher) {
                execute(params).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    abstract suspend fun execute(params: P): R
}