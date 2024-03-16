package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns [result] if this result [is ok][Result.isOk], otherwise [this].
 *
 * - Rust: [Result.and](https://doc.rust-lang.org/std/result/enum.Result.html#method.and)
 */
public infix fun <V, E, U> Result<V, E>.and(result: Result<U, E>): Result<U, E> {
    return when {
        isOk -> result
        else -> this.asErr()
    }
}

@Deprecated("Use andThen instead", ReplaceWith("andThen { result() }"))
public inline infix fun <V, E, U> Result<V, E>.and(result: () -> Result<U, E>): Result<U, E> {
    contract {
        callsInPlace(result, InvocationKind.AT_MOST_ONCE)
    }

    return andThen { result() }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function if this result [is ok][Result.isOk], or returning [this].
 *
 * - Elm: [Result.andThen](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#andThen)
 * - Rust: [Result.and_then](https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then)
 */
public inline infix fun <V, E, U> Result<V, E>.andThen(transform: (V) -> Result<U, E>): Result<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> transform(value)
        else -> this.asErr()
    }
}
