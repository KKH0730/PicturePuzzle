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

fun <T> kotlin.Result<T>.onResponseWithDefaultValue(
    onSuccess: (() -> Unit)? = null,
    onFailure: ((Throwable) -> Unit)? = null,
    defaultValue: T,
): T =
    (this@onResponseWithDefaultValue)
        .onSuccess { onSuccess?.invoke() }
        .onFailure {
            it.printStackTrace()
            Timber.e(it)
            onFailure?.invoke(it)
        }.getOrDefault(defaultValue)
