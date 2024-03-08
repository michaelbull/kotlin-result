package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns [result] if this [Result] is [Err], otherwise this [Ok].
 *
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 */
public infix fun <V, E, F> Result<V, E>.or(result: Result<V, F>): Result<V, F> {
    return when (this) {
        is Ok -> this
        is Err -> result
    }
}

@Deprecated("Use orElse instead", ReplaceWith("orElse { result() }"))
public inline infix fun <V, E, F> Result<V, E>.or(result: () -> Result<V, F>): Result<V, F> {
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
public inline infix fun <V, E, F> Result<V, E>.orElse(transform: (E) -> Result<V, F>): Result<V, F> {
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
 * Throws the [error][Err.error] if this [Result] is an [Err] and satisfies the given
 * [predicate], otherwise returns this [Result].
 *
 * @see [takeIf]
 */
public inline fun <V, E : Throwable> Result<V, E>.throwIf(predicate: (E) -> Boolean): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        this is Err && predicate(error) -> throw error
        else -> this
    }
}

/**
 * Throws the [error][Err.error] if this [Result] is an [Err] and _does not_ satisfy the
 * given [predicate], otherwise returns this [Result].
 *
 * @see [takeUnless]
 */
public inline fun <V, E : Throwable> Result<V, E>.throwUnless(predicate: (E) -> Boolean): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> if (!predicate(error)) {
            throw error
        } else {
            this
        }
    }
}
