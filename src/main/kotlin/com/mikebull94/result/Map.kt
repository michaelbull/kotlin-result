package com.mikebull94.result

/**
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 */
inline fun <V, E, U> Result<V, E>.map(transform: (V) -> U): Result<U, E> {
    return when (this) {
        is Ok -> ok(transform(value))
        is Error -> error(error)
    }
}

/**
 * - Elm: [Result.mapError](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#mapError)
 * - Haskell: [Data.Bifunctor.right](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:second)
 */
inline fun <V, E, U> Result<V, E>.mapError(transform: (E) -> U): Result<V, U> {
    return when (this) {
        is Ok -> ok(value)
        is Error -> error(transform(error))
    }
}

/**
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
inline fun <V, E, U> Result<V, E>.mapBoth(success: (V) -> U, failure: (E) -> U): U {
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
    okTransform: (V1) -> V2,
    errorTransform: (E1) -> E2
): Result<V2, E2> {
    return when (this) {
        is Ok -> ok(okTransform(value))
        is Error -> error(errorTransform(error))
    }
}
