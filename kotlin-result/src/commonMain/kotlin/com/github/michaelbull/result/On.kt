package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Invokes an [action] if this result [is ok][Result.isOk].
 *
 * - Rust: [Result.inspect](https://doc.rust-lang.org/std/result/enum.Result.html#method.inspect)
 */
@IgnorableReturnValue
public inline infix fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (isOk) {
        action(value)
    }

    return this
}

/**
 * Invokes an [action] if this result [is an error][Result.isErr].
 *
 * - Rust [Result.inspect_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.inspect_err)
 */
@IgnorableReturnValue
public inline infix fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (isErr) {
        action(error)
    }

    return this
}
