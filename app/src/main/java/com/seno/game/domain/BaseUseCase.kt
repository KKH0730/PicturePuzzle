package com.seno.game.domain

import com.seno.game.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseUseCase<P, R>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(params: P): Flow<Result<R>> {
        return flow {
            kotlin.runCatching {
                execute(params = params)
            }.onSuccess {
                emit(Result.Success(it))
            }.onFailure {
                emit(Result.Error(it))
            }
        }
    }

    abstract suspend fun execute(params: P): R
}