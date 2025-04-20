package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmField
import kotlin.jvm.Transient

private typealias Producer<T, E> = suspend CoroutineScope.() -> Result<T, E>

@PublishedApi
internal class ParZipException(
    @JvmField @Transient val error: Any?
) : RuntimeException("parZip failed with error: $error")

@PublishedApi
internal inline val <V, E> Result<V, E>.valueOrThrowParZipException: V
    get() = getOrThrow(::ParZipException)


@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <E> ParZipException.toErr(): Result<Nothing, E> = Err(error as E)


/**
 * Runs [producer1] and [producer2] in parallel on [context], combining their successful results with [transform].
 * If either computation fails with an [Err], the other is cancelled, and the error is returned as [Err].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [context] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor],
 * this function will not run [producer1], [producer2] in parallel.
 */
public suspend inline fun <T1, T2, E, V> parZip(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline producer1: Producer<T1, E>,
    crossinline producer2: Producer<T2, E>,
    crossinline transform: suspend CoroutineScope.(value1: T1, value2: T2) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        coroutineScope {
            val d1 = async(context) { producer1().valueOrThrowParZipException }
            val d2 = async(context) { producer2().valueOrThrowParZipException }
            val values = awaitAll(d1, d2)
            Ok(
                @Suppress("UNCHECKED_CAST")
                transform(values[0] as T1, values[1] as T2)
            )
        }
    } catch (e: ParZipException) {
        e.toErr()
    }
}

/**
 * Runs [producer1] and [producer2] in parallel on [Dispatchers.Default], combining their successful results with [transform].
 * If either computation fails with an [Err], the other is cancelled, and the error is returned as [Err].
 */
public suspend inline fun <T1, T2, E, V> parZip(
    crossinline producer1: Producer<T1, E>,
    crossinline producer2: Producer<T2, E>,
    crossinline transform: suspend CoroutineScope.(value1: T1, value2: T2) -> V,
): Result<V, E> = parZip(Dispatchers.Default, producer1, producer2, transform)


/**
 * Runs [producer1], [producer2], and [producer3] in parallel on [context], combining their successful results with [transform].
 * If any computation fails with an [Err], the others are cancelled, and the error is returned as [Err].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with the [context] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single-threaded [ContinuationInterceptor],
 * this function will not run [producer1], [producer2], and [producer3] in parallel.
 */
public suspend inline fun <T1, T2, T3, E, V> parZip(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline producer1: Producer<T1, E>,
    crossinline producer2: Producer<T2, E>,
    crossinline producer3: Producer<T3, E>,
    crossinline transform: suspend CoroutineScope.(T1, T2, T3) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        coroutineScope {
            val d1 = async(context) { producer1().valueOrThrowParZipException }
            val d2 = async(context) { producer2().valueOrThrowParZipException }
            val d3 = async(context) { producer3().valueOrThrowParZipException }
            val values = awaitAll(d1, d2, d3)
            Ok(
                @Suppress("UNCHECKED_CAST")
                transform(
                    values[0] as T1,
                    values[1] as T2,
                    values[2] as T3
                )
            )
        }
    } catch (e: ParZipException) {
        e.toErr()
    }
}

/**
 * Runs [producer1], [producer2], [producer3], and [producer4] in parallel on [context], combining their successful results with [transform].
 * If any computation fails with an [Err], the others are cancelled, and the error is returned as [Err].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with the [context] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single-threaded [ContinuationInterceptor],
 * this function will not run [producer1], [producer2], [producer3], and [producer4] in parallel.
 */
public suspend inline fun <T1, T2, T3, T4, E, V> parZip(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline producer1: Producer<T1, E>,
    crossinline producer2: Producer<T2, E>,
    crossinline producer3: Producer<T3, E>,
    crossinline producer4: Producer<T4, E>,
    crossinline transform: suspend CoroutineScope.(T1, T2, T3, T4) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        coroutineScope {
            val d1 = async(context) { producer1().valueOrThrowParZipException }
            val d2 = async(context) { producer2().valueOrThrowParZipException }
            val d3 = async(context) { producer3().valueOrThrowParZipException }
            val d4 = async(context) { producer4().valueOrThrowParZipException }
            val values = awaitAll(d1, d2, d3, d4)
            Ok(
                @Suppress("UNCHECKED_CAST")
                transform(
                    values[0] as T1,
                    values[1] as T2,
                    values[2] as T3,
                    values[3] as T4
                )
            )
        }
    } catch (e: ParZipException) {
        e.toErr()
    }
}

/**
 * Runs [producer1], [producer2], [producer3], [producer4], and [producer5] in parallel on [context], combining their successful results with [transform].
 * If any computation fails with an [Err], the others are cancelled, and the error is returned as [Err].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with the [context] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single-threaded [ContinuationInterceptor],
 * this function will not run [producer1], [producer2], [producer3], [producer4], and [producer5] in parallel.
 */
public suspend inline fun <T1, T2, T3, T4, T5, E, V> parZip(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline producer1: Producer<T1, E>,
    crossinline producer2: Producer<T2, E>,
    crossinline producer3: Producer<T3, E>,
    crossinline producer4: Producer<T4, E>,
    crossinline producer5: Producer<T5, E>,
    crossinline transform: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer5, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        coroutineScope {
            val d1 = async(context) { producer1().valueOrThrowParZipException }
            val d2 = async(context) { producer2().valueOrThrowParZipException }
            val d3 = async(context) { producer3().valueOrThrowParZipException }
            val d4 = async(context) { producer4().valueOrThrowParZipException }
            val d5 = async(context) { producer5().valueOrThrowParZipException }
            val values = awaitAll(d1, d2, d3, d4, d5)
            Ok(
                @Suppress("UNCHECKED_CAST")
                transform(
                    values[0] as T1,
                    values[1] as T2,
                    values[2] as T3,
                    values[3] as T4,
                    values[4] as T5
                )
            )
        }
    } catch (e: ParZipException) {
        e.toErr()
    }
}
