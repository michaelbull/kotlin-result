package com.github.michaelbull.result

public actual object BindingException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
