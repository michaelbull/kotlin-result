package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.throwIf
import kotlinx.coroutines.CancellationException

/**
 * Calls the specified function [block] and returns its encapsulated result if
 * invocation was successful, catching and encapsulating any thrown exception
 * as a failure, excepting that any [CancellationException] will be rethrown
 * in order to propagate cancellation from any parent
 * [CoroutineContext][kotlin.coroutines.CoroutineContext].
 *
 * @throws CancellationException if is thrown from the block
 */
public suspend inline fun <V> runSuspendCatching(block: () -> V)
    : Result<V, Throwable> =
    runCatching(block)
        .throwIf { it is CancellationException }
/**
 * Calls the specified function [block] with [this] value as its receiver and
 * returns its encapsulated result if invocation was successful, catching and
 * encapsulating any thrown exception as a failure, excepting that any
 * [CancellationException] will be rethrown in order to propagate cancellation
 * from any parent [CoroutineContext][kotlin.coroutines.CoroutineContext].
 *
 * @throws CancellationException if is thrown from the block
 */
public suspend inline fun <V,T> T.runSuspendCatching(block: T.() -> V)
    : Result<V, Throwable> =
    runCatching(block)
        .throwIf { it is CancellationException }
