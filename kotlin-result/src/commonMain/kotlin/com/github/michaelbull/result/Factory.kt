package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was
 * successful, catching any [Throwable] exception that was thrown from the [block] function
 * execution and encapsulating it as a failure.
 */
public inline fun <V> runCatching(block: () -> V): Result<V, Throwable> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        Ok(block())
    } catch (e: Throwable) {
        Err(e)
    }
}

/**
 * Calls the specified function [block] with [this] value as its receiver and returns its
 * encapsulated result if invocation was successful, catching any [Throwable] exception that was
 * thrown from the [block] function execution and encapsulating it as a failure.
 */
public inline infix fun <T, V> T.runCatching(block: T.() -> V): Result<V, Throwable> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        Ok(block())
    } catch (e: Throwable) {
        Err(e)
    }
}

/**
 * Converts a nullable of type [V] to a [Result]. Returns [Ok] if the value is
 * non-null, otherwise the supplied [error].
 */
public inline infix fun <V, E> V?.toResultOr(error: () -> E): Result<V, E> {
    contract {
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        null -> Err(error())
        else -> Ok(this)
    }
}
