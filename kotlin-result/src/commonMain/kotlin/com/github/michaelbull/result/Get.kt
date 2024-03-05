package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the [value][Result.value] if this result [is ok][Result.isOk], otherwise `null`.
 *
 * - Elm: [Result.toMaybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#toMaybe)
 * - Rust: [Result.ok](https://doc.rust-lang.org/std/result/enum.Result.html#method.ok)
 */
public fun <V, E> Result<V, E>.get(): V? {
    return when {
        isOk -> value
        else -> null
    }
}

/**
 * Returns the [error][Result.error] if this result [is an error][Result.isErr], otherwise `null`.
 *
 * - Rust: [Result.err](https://doc.rust-lang.org/std/result/enum.Result.html#method.err)
 */
public fun <V, E> Result<V, E>.getError(): E? {
    return when {
        isErr -> error
        else -> null
    }
}

/**
 * Returns the [value][Result.value] if this result [is ok][Result.isOk], otherwise [default].
 *
 * - Elm: [Result.withDefault](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#withDefault)
 * - Haskell: [Result.fromLeft](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromLeft)
 * - Rust: [Result.unwrap_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_or)
 *
 * @param default The value to return if [Err].
 * @return The [value][Result.value] if [Ok], otherwise [default].
 */
public infix fun <V, E> Result<V, E>.getOr(default: V): V {
    return when {
        isOk -> value
        else -> default
    }
}

/**
 * Returns the [error][Result.error] if this result [is an error][Result.isErr], otherwise
 * [default].
 *
 * - Haskell: [Result.fromRight](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromRight)
 *
 * @param default The error to return if [Ok].
 * @return The [error][Result.error] if [Err], otherwise [default].
 */
public infix fun <V, E> Result<V, E>.getErrorOr(default: E): E {
    return when {
        isOk -> default
        else -> error
    }
}

/**
 * Returns the [value][Result.value] if this result [is ok][Result.isOk], otherwise the
 * [transformation][transform] of the [error][Result.error].
 *
 * - Elm: [Result.extract](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#extract)
 * - Rust: [Result.unwrap_or_else](https://doc.rust-lang.org/src/core/result.rs.html#735-740)
 */
public inline infix fun <V, E> Result<V, E>.getOrElse(transform: (E) -> V): V {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> value
        else -> transform(error)
    }
}

/**
 * Returns the [error][Result.error] if this result [is an error][Result.isErr], otherwise the
 * [transformation][transform] of the [value][Result.value].
 */
public inline infix fun <V, E> Result<V, E>.getErrorOrElse(transform: (V) -> E): E {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr -> error
        else -> transform(value)
    }
}

/**
 * Returns the [value][Result.value] if this result [is ok][Result.isOk], otherwise throws the
 * [error][Result.error].
 *
 * This is functionally equivalent to [`getOrElse { throw it }`][getOrElse].
 */
public fun <V, E : Throwable> Result<V, E>.getOrThrow(): V {
    return when {
        isOk -> value
        else -> throw error
    }
}

/**
 * Returns the [value][Result.value] if this result [is ok][Result.isOk], otherwise throws the
 * [transformation][transform] of the [error][Result.error] to a [Throwable].
 */
public inline infix fun <V, E> Result<V, E>.getOrThrow(transform: (E) -> Throwable): V {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> value
        else -> throw transform(error)
    }
}

/**
 * Merges this [Result<V, E>][Result] to [U], returning the [value][Result.value] if this result
 * [is ok][Result.isOk], otherwise the [error][Result.error].
 *
 * - Scala: [MergeableEither.merge](https://www.scala-lang.org/api/2.12.0/scala/util/Either$$MergeableEither.html#merge:A)
 */
public fun <V : U, E : U, U> Result<V, E>.merge(): U {
    return when {
        isOk -> value
        else -> error
    }
}
