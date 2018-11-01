package com.github.michaelbull.result

@Deprecated("Use lazy-evaluating variant instead", ReplaceWith("or { result }"))
infix fun <V, E> Result<V, E>.or(result: Result<V, E>): Result<V, E> {
    return or { result }
}

/**
 * Returns [result] if this [Result] is [Err], otherwise this [Ok].
 *
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 */
inline infix fun <V, E> Result<V, E>.or(result: () -> Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> this
        is Err -> result()
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err],
 * otherwise this [Ok].
 *
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 */
inline infix fun <V, E> Result<V, E>.orElse(transform: (E) -> Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> this
        is Err -> transform(error)
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err],
 * otherwise this [Ok].
 */
inline infix fun <V, E> Result<V, E>.recover(transform: (E) -> V): Ok<V> {
    return when (this) {
        is Ok -> this
        is Err -> Ok(transform(error))
    }
}
