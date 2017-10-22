package com.github.michaelbull.result

/**
 * Maps a [Result<V, E>][Result] to [Result<U, E>][Result] by applying a function to a contained
 * [Ok] value, leaving an [Err] value untouched.
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 *
 * @param transform The transformation to apply to the [value][Ok.value]
 * @return The [transformed][transform] [Result] if [Ok], otherwise [Err].
 */
infix inline fun <V, E, U> Result<V, E>.map(transform: (V) -> U): Result<U, E> {
    return when (this) {
        is Ok -> Ok(transform(value))
        is Err -> this
    }
}

/**
 * Maps a [Result<V, E>][Result] to [Result<V, F>][Result] by applying a function to a contained
 * [Err] value, leaving an [Ok] value untouched.
 *
 * - Elm: [Result.mapError](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#mapError)
 * - Haskell: [Data.Bifunctor.right](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:second)
 * - Rust: [Result.map_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_err)
 *
 * @param transform The transformation to apply to the [error][Err.error].
 * @return The [value][Ok.value] if [Ok], otherwise the [transformed][transform] [Err].
 */
infix inline fun <V, E, F> Result<V, E>.mapError(transform: (E) -> F): Result<V, F> {
    return when (this) {
        is Ok -> this
        is Err -> Err(transform(error))
    }
}

/**
 * Map a [Result<V, E>][Result] to `U` by applying either the [success] function if the [Result]
 * is [Ok] or the [failure] function if the [Result] is an [Err]. Both of these functions must
 * return the same type (`U`).
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 *
 * @param success The function to apply to `V` if [Ok].
 * @param failure The function to apply to `E` if [Err].
 * @return The mapped value.
 */
inline fun <V, E, U> Result<V, E>.mapBoth(
    success: (V) -> U,
    failure: (E) -> U
): U {
    return when (this) {
        is Ok -> success(value)
        is Err -> failure(error)
    }
}

// TODO: better name?
/**
 * Map a [Result<V, E>][Result] to [Result<U, F>][Result] by applying either the [success] function
 * if the [Result] is [Ok] or the [failure] function if the [Result] is an [Err].
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 *
 * @param success The function to apply to `V` if [Ok].
 * @param failure The function to apply to `E` if [Err].
 * @return The mapped [Result].
 */
inline fun <V, E, U, F> Result<V, E>.mapEither(
    success: (V) -> U,
    failure: (E) -> F
): Result<U, F> {
    return when (this) {
        is Ok -> Ok(success(value))
        is Err -> Err(failure(error))
    }
}
