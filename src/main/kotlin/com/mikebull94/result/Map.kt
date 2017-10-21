package com.mikebull94.result

/**
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
infix inline fun <V, E, U> Result<V, E>.map(transform: (V) -> U): Result<U, E> {
    return when (this) {
        is Ok -> ok(transform(value))
        is Error -> err(error)
    }
}

/**
 * - Elm: [Result.mapError](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#mapError)
 * - Haskell: [Data.Bifunctor.right](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:second)
 * - Rust: [Result.map_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_err)
 */
infix inline fun <V, E, U> Result<V, E>.mapError(transform: (E) -> U): Result<V, U> {
    return when (this) {
        is Ok -> ok(value)
        is Error -> err(transform(error))
    }
}

/**
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
inline fun <V, E, U> Result<V, E>.mapBoth(
    success: (V) -> U,
    failure: (E) -> U
): U {
    return when (this) {
        is Ok -> success(value)
        is Error -> failure(error)
    }
}

// TODO: better name?
/**
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
inline fun <V1, V2, E1, E2> Result<V1, E1>.mapEither(
    success: (V1) -> V2,
    failure: (E1) -> E2
): Result<V2, E2> {
    return when (this) {
        is Ok -> ok(success(value))
        is Error -> err(failure(error))
    }
}
