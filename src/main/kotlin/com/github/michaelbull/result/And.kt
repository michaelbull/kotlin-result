package com.github.michaelbull.result

/**
 * Returns [result] if this [Result] is [Ok], otherwise this [Err].
 *
 * - Rust: [Result.and](https://doc.rust-lang.org/std/result/enum.Result.html#method.and)
 *
 * @param result The [Result] to return if [Ok].
 * @return The [result] if [Ok], otherwise [Err].
 */
infix fun <V, E> Result<V, E>.and(result: Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> result
        is Err -> this
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform] function
 * if this [Result] is [Ok], or returning this [Err].
 *
 * - Elm: [Result.andThen](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#andThen)
 * - Rust: [Result.and_then](https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then)
 *
 * @param transform The transformation to apply to the [value][Ok.value].
 * @return The [transformed][transform] [Result] if [Ok], otherwise [Err].
 */
infix inline fun <V, E, U> Result<V, E>.andThen(transform: (V) -> Result<U, E>): Result<U, E> {
    return when (this) {
        is Ok -> transform(value)
        is Err -> this
    }
}
