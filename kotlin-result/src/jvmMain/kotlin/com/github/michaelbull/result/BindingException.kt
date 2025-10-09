package com.github.michaelbull.result

public actual class BindingException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
