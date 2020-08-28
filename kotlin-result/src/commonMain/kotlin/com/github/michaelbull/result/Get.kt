package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the [value][Ok.value] if this [Result] is [Ok], otherwise `null`.
 *
 * - Elm: [Result.toMaybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#toMaybe)
 * - Rust: [Result.ok](https://doc.rust-lang.org/std/result/enum.Result.html#method.ok)
 */
public fun <V, E> Result<V, E>.get(): V? {
    contract {
        returnsNotNull() implies (this@get is Ok<V>)
        returns(null) implies (this@get is Err<E>)
    }

    return when (this) {
        is Ok -> value
        is Err -> null
    }
}

/**
 * Returns the [error][Err.error] if this [Result] is [Err], otherwise `null`.
 *
 * - Rust: [Result.err](https://doc.rust-lang.org/std/result/enum.Result.html#method.err)
 */
public fun <V, E> Result<V, E>.getError(): E? {
    contract {
        returns(null) implies (this@getError is Ok<V>)
        returnsNotNull() implies (this@getError is Err<E>)
    }

    return when (this) {
        is Ok -> null
        is Err -> error
    }
}

/**
 * Returns the [value][Ok.value] if this [Result] is [Ok], otherwise [default].
 *
 * - Elm: [Result.withDefault](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#withDefault)
 * - Haskell: [Result.fromLeft](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromLeft)
 * - Rust: [Result.unwrap_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_or)
 *
 * @param default The value to return if [Err].
 * @return The [value][Ok.value] if [Ok], otherwise [default].
 */
public infix fun <V, E> Result<V, E>.getOr(default: V): V {
    return when (this) {
        is Ok -> value
        is Err -> default
    }
}

@Deprecated("Use getOrElse instead", ReplaceWith("getOrElse { default() }"))
public inline infix fun <V, E> Result<V, E>.getOr(default: () -> V): V {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }

    return getOrElse { default() }
}

/**
 * Returns the [error][Err.error] if this [Result] is [Err], otherwise [default].
 *
 * - Haskell: [Result.fromRight](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromRight)
 *
 * @param default The error to return if [Ok].
 * @return The [error][Err.error] if [Err], otherwise [default].
 */
public infix fun <V, E> Result<V, E>.getErrorOr(default: E): E {
    return when (this) {
        is Ok -> default
        is Err -> error
    }
}

@Deprecated("Use getOrElse instead", ReplaceWith("getErrorOrElse { default() }"))
public inline infix fun <V, E> Result<V, E>.getErrorOr(default: () -> E): E {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }

    return getErrorOrElse { default() }
}

/**
 * Returns the [value][Ok.value] if this [Result] is [Ok], otherwise the
 * [transformation][transform] of the [error][Err.error].
 *
 * - Elm: [Result.extract](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#extract)
 * - Rust: [Result.unwrap_or_else](https://doc.rust-lang.org/src/core/result.rs.html#735-740)
 */
public inline infix fun <V, E> Result<V, E>.getOrElse(transform: (E) -> V): V {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> value
        is Err -> transform(error)
    }
}

/**
 * Returns the [error][Err.error] if this [Result] is [Err], otherwise the
 * [transformation][transform] of the [value][Ok.value].
 */
public inline infix fun <V, E> Result<V, E>.getErrorOrElse(transform: (V) -> E): E {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> transform(value)
        is Err -> error
    }
}

/**
 * Merges this [Result<V, E>][Result] to [U], returning the [value][Ok.value] if this [Result] is [Ok], otherwise the
 * [error][Err.error].
 *
 * - Scala: [MergeableEither.merge](https://www.scala-lang.org/api/2.12.0/scala/util/Either$$MergeableEither.html#merge:A)
 */
public fun <V : U, E : U, U> Result<V, E>.merge(): U {
    return when (this) {
        is Ok -> value
        is Err -> error
    }
}
