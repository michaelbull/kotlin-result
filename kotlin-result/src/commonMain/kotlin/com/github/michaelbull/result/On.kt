package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Invokes an [action] if this [Result] is [Ok].
 */
public inline infix fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Ok) {
        action(value)
    }

    return this
}

/**
 * Invokes an [action] if this [Result] is [Err].
 */
public inline infix fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Err) {
        action(error)
    }

    return this
}
