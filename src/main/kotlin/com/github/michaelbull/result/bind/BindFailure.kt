package com.github.michaelbull.result.bind

sealed class NoStackTraceException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }

    object BindFailure : NoStackTraceException()
}
