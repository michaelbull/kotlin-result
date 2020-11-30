package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function to the [value][Ok.value] if this [Result] is [Ok], or returning this [Err].
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
public inline infix fun <V, E, U> Result<V, E>.map(transform: (V) -> U): Result<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

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
public inline infix fun <V, E, F> Result<V, E>.mapError(transform: (E) -> F): Result<V, F> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> Err(transform(error))
    }
}

/**
 * Maps this [Result<V, E][Result] to [U] by either applying the [transform] function to the
 * [value][Ok.value] if this [Result] is [Ok], or returning the [default] if this [Result] is an
 * [Err].
 *
 * - Rust: [Result.map_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_or)
 */
public inline fun <V, E, U> Result<V, E>.mapOr(default: U, transform: (V) -> U): U {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> transform(value)
        is Err -> default
    }
}

/**
 * Maps this [Result<V, E>][Result] to [U] by applying either the [transform] function if this
 * [Result] is [Ok], or the [default] function if this [Result] is an [Err]. Both of these
 * functions must return the same type ([U]).
 *
 * - Rust: [Result.map_or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_or_else)
 */
public inline fun <V, E, U> Result<V, E>.mapOrElse(default: (E) -> U, transform: (V) -> U): U {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> transform(value)
        is Err -> default(error)
    }
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails.
 */
public inline infix fun <V, E, U> Result<Iterable<V>, E>.mapAll(transform: (V) -> Result<U, E>): Result<List<U>, E> {
    return map { iterable ->
        iterable.map { element ->
            when (val transformed = transform(element)) {
                is Ok -> transformed.value
                is Err -> return transformed
            }
        }
    }
}

/**
 * Maps this [Result<V, E>][Result] to [U] by applying either the [success] function if this
 * [Result] is [Ok], or the [failure] function if this [Result] is an [Err]. Both of these
 * functions must return the same type ([U]).
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> Result<V, E>.mapBoth(success: (V) -> U, failure: (E) -> U): U {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> success(value)
        is Err -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][Result] to [U] by applying either the [success] function if this
 * [Result] is [Ok], or the [failure] function if this [Result] is an [Err]. Both of these
 * functions must return the same type ([U]).
 *
 * This is functionally equivalent to [mapBoth].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> Result<V, E>.fold(success: (V) -> U, failure: (E) -> U): U {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return mapBoth(success, failure)
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, F>][Result] by applying either the [success]
 * function if this [Result] is [Ok], or the [failure] function if this [Result] is an [Err].
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
public inline fun <V, E, U, F> Result<V, E>.mapEither(success: (V) -> U, failure: (E) -> F): Result<U, F> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

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
public inline infix fun <V, E, U> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return andThen(transform)
}

/**
 * Returns the [transformation][transform] of the [value][Ok.value] if this [Result] is [Ok]
 * and satisfies the given [predicate], otherwise this [Result].
 *
 * @see [takeIf]
 */
public inline fun <V, E> Result<V, E>.toErrorIf(predicate: (V) -> Boolean, transform: (V) -> E): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> if (predicate(value)) {
            Err(transform(value))
        } else {
            this
        }
        is Err -> this
    }
}

/**
 * Returns the [transformation][transform] of the [value][Ok.value] if this [Result] is [Ok]
 * and _does not_ satisfy the given [predicate], otherwise this [Result].
 *
 * @see [takeUnless]
 */
public inline fun <V, E> Result<V, E>.toErrorUnless(predicate: (V) -> Boolean, transform: (V) -> E): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> if (!predicate(value)) {
            Err(transform(value))
        } else {
            this
        }
        is Err -> this
    }
}
