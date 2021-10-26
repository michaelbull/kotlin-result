package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns [result] if this [Result] is [Err], otherwise this [Ok].
 *
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 */
public infix fun <V, E> Result<V, E>.or(result: Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> this
        is Err -> result
    }
}

@Deprecated("Use orElse instead", ReplaceWith("orElse { result() }"))
public inline infix fun <V, E> Result<V, E>.or(result: () -> Result<V, E>): Result<V, E> {
    contract {
        callsInPlace(result, InvocationKind.AT_MOST_ONCE)
    }

    return orElse { result() }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err],
 * otherwise this [Ok].
 *
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 */
public inline infix fun <V, E> Result<V, E>.orElse(transform: (E) -> Result<V, E>): Result<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> transform(error)
    }
}

/**
 * Throws the [error][Err.error] if this [Result] is [Err], otherwise returns this [Ok].
 */
public fun <V, E : Throwable> Result<V, E>.orElseThrow(): Ok<V> {
    return when (this) {
        is Ok -> this
        is Err -> throw error
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err],
 * otherwise this [Ok].
 */
public inline infix fun <V, E> Result<V, E>.recover(transform: (E) -> V): Ok<V> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> Ok(transform(error))
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err]
 * and satisfies the given [predicate], otherwise this [Result].
 */
public inline fun <V, E> Result<V, E>.recoverIf(predicate: (E) -> Boolean, transform: (E) -> V): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> if (predicate(error)) {
            Ok(transform(error))
        } else {
            this
        }
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err]
 * and _does not_ satisfy the given [predicate], otherwise this [Result].
 */
public inline fun <V, E> Result<V, E>.recoverUnless(predicate: (E) -> Boolean, transform: (E) -> V): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> if (!predicate(error)) {
            Ok(transform(error))
        } else {
            this
        }
    }
}
