package com.github.michaelbull.result

internal actual object BindException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
