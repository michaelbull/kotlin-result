package com.github.michaelbull.result

/**
 * If the [Result] is [Err] and contains a [Throwable], throws the [error][Err.error]
 * if the [predicate] returns true.
 *
 * @throws E if [predicate] returns true.
 */
public inline fun <V : Any?, E : Throwable> Result<V, E>.throwIf(
    predicate: (E) -> Boolean
): Result<V, E> =
    onFailure { e -> if (predicate(e)) throw e }

/**
 * If the [Result] is [Err] and contains a [Throwable], throws the [error][Err.error]
 * only if the [predicate] returns false.
 *
 * @throws E if [predicate] returns false.
 */
public inline fun <V : Any?, E : Throwable> Result<V, E>.throwUnless(
    predicate: (E) -> Boolean
): Result<V, E> = throwIf { e -> predicate(e).not() }
