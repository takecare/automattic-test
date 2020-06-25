package com.automattic.freshlypressed.domain

sealed class Result<T> {
    data class Success<T>(val content: T) : Result<T>()
    data class Error<T>(val error: Throwable) : Result<T>()
}
