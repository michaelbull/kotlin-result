package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns [result] if this result [is an error][Result.isErr], otherwise [this].
 *
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 */
public infix fun <V, E, F> Result<V, E>.or(result: Result<V, F>): Result<V, F> {
    return when {
        isOk -> this.asOk()
        else -> result
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr], otherwise [this].
 *
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 */
public inline infix fun <V, E, F> Result<V, E>.orElse(transform: (E) -> Result<V, F>): Result<V, F> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> this.asOk()
        else -> transform(error)
    }
}

/**
 * Throws the [error][Result.error] if this result [is an error][Result.isErr], otherwise returns
 * [this].
 */
public fun <V, E : Throwable> Result<V, E>.orElseThrow(): Result<V, Nothing> {
    return when {
        isOk -> this.asOk()
        else -> throw error
    }
}

/**
 * Throws the [error][Result.error] if this result [is an error][Result.isErr] and satisfies the
 * given [predicate], otherwise returns [this].
 *
 * @see [takeIf]
 */
public inline fun <V, E : Throwable> Result<V, E>.throwIf(predicate: (E) -> Boolean): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && predicate(error) -> throw error
        else -> this
    }
}

/**
 * Throws the [error][Result.error] if this result [is an error][Result.isErr] and _does not_
 * satisfy the given [predicate], otherwise returns [this].
 *
 * @see [takeUnless]
 */
public inline fun <V, E : Throwable> Result<V, E>.throwUnless(predicate: (E) -> Boolean): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && !predicate(error) -> throw error
        else -> this
    }
}
