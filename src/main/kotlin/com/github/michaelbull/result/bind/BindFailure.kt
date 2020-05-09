package com.github.michaelbull.result.bind

internal object BindFailure : Exception() {
    // no stacktrace needed
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
