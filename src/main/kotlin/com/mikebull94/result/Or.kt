package com.mikebull94.result

/**
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 *
 * @param result The [Result] to return if [Error].
 * @return The [result] if [Error], otherwise [ok].
 */
infix fun <V, E> Result<V, E>.or(result: Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> ok(value)
        is Error -> result
    }
}

/**
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 *
 * @param transform The transformation to apply to the [error][Error.error].
 * @return The [transformed][transform] [Result] if [Error], otherwise [ok].
 */
infix inline fun <V, E> Result<V, E>.orElse(transform: (E) -> Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> ok(this.value)
        is Error -> transform(error)
    }
}
