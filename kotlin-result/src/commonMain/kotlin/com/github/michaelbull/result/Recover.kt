package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr], otherwise [this].
 */
public inline infix fun <V, E> Result<V, E>.recover(transform: (E) -> V): Result<V, Nothing> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> this.asOk()
        else -> Ok(transform(error))
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr], catching and encapsulating any thrown exception as an [Err],
 * otherwise [this].
 */
public inline infix fun <V, E> Result<V, E>.recoverCatching(transform: (E) -> V): Result<V, Throwable> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> this.asOk()
        else -> runCatching { transform(error) }
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr] and satisfies the given [predicate], otherwise [this].
 */
public inline fun <V, E> Result<V, E>.recoverIf(
    predicate: (E) -> Boolean,
    transform: (E) -> V,
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && predicate(error) -> Ok(transform(error))
        else -> this
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr] and _does not_ satisfy the given [predicate], otherwise [this].
 */
public inline fun <V, E> Result<V, E>.recoverUnless(predicate: (E) -> Boolean, transform: (E) -> V): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && !predicate(error) -> Ok(transform(error))
        else -> this
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr], otherwise [this].
 */
public inline fun <V, E> Result<V, E>.andThenRecover(transform: (E) -> Result<V, E>): Result<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> this
        else -> transform(error)
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr] and satisfies the given [predicate], otherwise [this].
 */
public inline fun <V, E> Result<V, E>.andThenRecoverIf(
    predicate: (E) -> Boolean,
    transform: (E) -> Result<V, E>,
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && predicate(error) -> transform(error)
        else -> this
    }
}

/**
 * Returns the [transformation][transform] of the [error][Result.error] if this result
 * [is an error][Result.isErr] and _does not_ satisfy the given [predicate], otherwise [this].
 */
public inline fun <V, E> Result<V, E>.andThenRecoverUnless(
    predicate: (E) -> Boolean,
    transform: (E) -> Result<V, E>,
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && !predicate(error) -> transform(error)
        else -> this
    }
}
