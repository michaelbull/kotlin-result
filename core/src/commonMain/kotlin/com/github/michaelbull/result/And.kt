package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Deprecated("Use lazy-evaluating variant instead", ReplaceWith("and { result }"))
infix fun <V, E> Result<V, E>.and(result: Result<V, E>): Result<V, E> {
    return and { result }
}

/**
 * Returns [result] if this [Result] is [Ok], otherwise this [Err].
 *
 * - Rust: [Result.and](https://doc.rust-lang.org/std/result/enum.Result.html#method.and)
 */
inline infix fun <V, E> Result<V, E>.and(result: () -> Result<V, E>): Result<V, E> {
    contract {
        callsInPlace(result, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> result()
        is Err -> this
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function if this [Result] is [Ok], or returning this [Err].
 *
 * - Elm: [Result.andThen](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#andThen)
 * - Rust: [Result.and_then](https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then)
 */
inline infix fun <V, E, U> Result<V, E>.andThen(transform: (V) -> Result<U, E>): Result<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> transform(value)
        is Err -> this
    }
}
