package com.seno.game.domain

import com.seno.game.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

abstract class BaseUseCase<P, R>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(params: P): Flow<Result<R>> {
        return execute(params = params)
            .catch {
                Timber.e(it)
                emit(Result.Error(Exception(it)))
            }.flowOn(dispatcher)
    }

    abstract suspend fun execute(params: P): Flow<Result<R>>
}