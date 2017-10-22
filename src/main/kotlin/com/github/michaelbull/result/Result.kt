package com.github.michaelbull.result

/**
 * [Result] is a type that represents either success ([Ok]) or failure ([Error]).
 *
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 * - Rust: [Result](https://doc.rust-lang.org/std/result/enum.Result.html)
 */
sealed class Result<out V, out E>

data class Ok<out V> constructor(val value: V) : Result<V, Nothing>()
data class Error<out E> constructor(val error: E) : Result<Nothing, E>()
