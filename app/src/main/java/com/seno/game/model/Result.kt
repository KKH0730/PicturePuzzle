package com.seno.game.model

sealed class Result<out R> {

    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

fun <T> Result<T>.successOr(fallback: T): T {
    return data ?: fallback
}