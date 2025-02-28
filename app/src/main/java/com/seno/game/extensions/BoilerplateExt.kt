package com.seno.game.extensions

import com.seno.game.data.model.Result
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
