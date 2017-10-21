package com.mikebull94.result

/**
 * Rust: [Result.and](https://doc.rust-lang.org/std/result/enum.Result.html#method.and)
 */
infix fun <V, E> Result<V, E>.and(result: Result<V, E>): Result<V, E> {
    return when(this) {
        is Ok -> result
        is Error -> err(error)
    }
}

/**
 * - Elm: [Result.andThen](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#andThen)
 * - Rust: [Result.and_then](https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then)
 */
infix inline fun <V, E, U> Result<V, E>.andThen(transform: (V) -> Result<U, E>): Result<U, E> {
    return when (this) {
        is Ok -> transform(value)
        is Error -> err(error)
    }
}
