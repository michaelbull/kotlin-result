package com.github.michaelbull.result

/**
 * Returns [result] if this [Result] is [Err], otherwise this [Ok].
 *
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 *
 * @param result The [Result] to return if [Err].
 * @return The [result] if [Err], otherwise [Ok].
 */
infix fun <V, E> Result<V, E>.or(result: Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> this
        is Err -> result
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err],
 * otherwise this [Ok].
 *
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 *
 * @param transform The transformation to apply to the [error][Err.error].
 * @return The [transformed][transform] [Result] if [Err], otherwise [Ok].
 */
infix inline fun <V, E> Result<V, E>.orElse(transform: (E) -> Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> this
        is Err -> transform(error)
    }
}
