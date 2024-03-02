package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Invokes an [action] if this [Result] is [Ok].
 *
 * - Rust: [Result.inspect](https://doc.rust-lang.org/std/result/enum.Result.html#method.inspect)
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
 *
 * - Rust [Result.inspect_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.inspect_err)
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
