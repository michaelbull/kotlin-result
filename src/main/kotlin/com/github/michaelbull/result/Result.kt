package com.github.michaelbull.result

/**
 * [Result] is a type that represents either success ([Ok]) or failure ([Err]).
 *
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 * - Rust: [Result](https://doc.rust-lang.org/std/result/enum.Result.html)
 */
sealed class Result<out V, out E> {
    companion object {

        /**
         * Invokes a [function] and wraps it in a [Result], returning an [Err] if a [Throwable]
         * was thrown, otherwise [Ok].
         */
        inline fun <T> of(function: () -> T): Result<T, Throwable> {
            return try {
                Ok(function.invoke())
            } catch (t: Throwable) {
                Err(t)
            }
        }
    }
}

/**
 * Represents a successful [Result], containing a [value].
 */
data class Ok<out V> constructor(val value: V) : Result<V, Nothing>()

/**
 * Represents a failed [Result], containing an [error] value.
 */
data class Err<out E> constructor(val error: E) : Result<Nothing, E>()
