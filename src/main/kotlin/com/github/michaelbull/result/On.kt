package com.github.michaelbull.result

/**
 * Invokes an [action] if this [Result] is [Ok].
 */
inline infix fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
    if (this is Ok) {
        action(value)
    }

    return this
}

/**
 * Invokes an [action] if this [Result] is [Err].
 */
inline infix fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
    if (this is Err) {
        action(error)
    }

    return this
}
