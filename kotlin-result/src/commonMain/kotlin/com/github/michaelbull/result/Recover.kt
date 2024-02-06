package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
 * Returns the [transformation][transform] of the [error][Err.error], catching and encapsulating any
 * thrown exception as a failure if this [Result] is [Err], otherwise this [Ok].
 */
public inline infix fun <V, E> Result<V, E>.recoverCatching(transform: (E) -> V): Result<V, Throwable> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> {
            try {
                Ok(transform(error))
            } catch (e: Throwable) {
                Err(e)
            }
        }
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

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err],
 * otherwise this [Result].
 */
public inline fun <V, E> Result<V, E>.andThenRecover(transform: (E) -> Result<V, E>): Result<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> transform(error)
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err] and
 * satisfies the given [predicate], otherwise this [Result].
 */
public inline fun <V, E> Result<V, E>.andThenRecoverIf(
    predicate: (E) -> Boolean,
    transform: (E) -> Result<V, E>
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> if (predicate(error)) {
            transform(error)
        } else {
            this
        }
    }
}

/**
 * Returns the [transformation][transform] of the [error][Err.error] if this [Result] is [Err]
 * and _does not_ satisfy the given [predicate], otherwise this [Result].
 */
public inline fun <V, E> Result<V, E>.andThenRecoverUnless(
    predicate: (E) -> Boolean,
    transform: (E) -> Result<V, E>
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> this
        is Err -> if (!predicate(error)) {
            transform(error)
        } else {
            this
        }
    }
}
