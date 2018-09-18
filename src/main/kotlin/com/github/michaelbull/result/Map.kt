package com.github.michaelbull.result

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function to the [value][Ok.value] if this [Result] is [Ok], or returning this [Err].
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
inline infix fun <V, E, U> Result<V, E>.map(transform: (V) -> U): Result<U, E> {
    return when (this) {
        is Ok -> Ok(transform(value))
        is Err -> this
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<V, F>][Result] by either applying the [transform]
 * function to the [error][Err.error] if this [Result] is [Err], or returning this [Ok].
 *
 * - Elm: [Result.mapError](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#mapError)
 * - Haskell: [Data.Bifunctor.right](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:second)
 * - Rust: [Result.map_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_err)
 */
inline infix fun <V, E, F> Result<V, E>.mapError(transform: (E) -> F): Result<V, F> {
    return when (this) {
        is Ok -> this
        is Err -> Err(transform(error))
    }
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails.
 */
inline infix fun <V, E, U> Result<Iterable<V>, E>.mapAll(transform: (V) -> Result<U, E>): Result<List<U>, E> {
    return map { iterable ->
        iterable.map { element ->
            val transformed = transform(element)

            when (transformed) {
                is Ok -> transformed.value
                is Err -> return transformed
            }
        }
    }
}

/**
 * Maps this [Result<V, E>][Result] to `U` by applying either the [success] function if this
 * [Result] is [Ok], or the [failure] function if this [Result] is an [Err]. Both of these
 * functions must return the same type (`U`).
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
inline fun <V, E, U> Result<V, E>.mapBoth(success: (V) -> U, failure: (E) -> U): U {
    return when (this) {
        is Ok -> success(value)
        is Err -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, F>][Result] by applying either the [success]
 * function if this [Result] is [Ok], or the [failure] function if this [Result] is an [Err].
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
inline fun <V, E, U, F> Result<V, E>.mapEither(success: (V) -> U, failure: (E) -> F): Result<U, F> {
    return when (this) {
        is Ok -> Ok(success(value))
        is Err -> Err(failure(error))
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function if this [Result] is [Ok], or returning this [Err].
 *
 * This is functionally equivalent to [andThen].
 *
 * - Scala: [Either.flatMap](http://www.scala-lang.org/api/2.12.0/scala/util/Either.html#flatMap[AA>:A,Y](f:B=>scala.util.Either[AA,Y]):scala.util.Either[AA,Y])
 */
inline infix fun <V, E, U> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> {
    return andThen(transform)
}
