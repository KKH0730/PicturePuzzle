package com.seno.game.extensions

import com.seno.game.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

fun <T> Flow<Result<T>>.catchError(dispatcher: CoroutineDispatcher): Flow<Result<T>> =
    (this@catchError).catch {
        Timber.e(it)
        emit(Result.Error(Exception(it)))
    }.flowOn(dispatcher)

inline fun <T> safeCall(block: () -> T): Result<T> {
    return runCatching { block() }
        .fold(
            onSuccess = { Result.Success(it) },
            onFailure = {
                Timber.e(it)
                Result.Error(it)
            }
        )
}