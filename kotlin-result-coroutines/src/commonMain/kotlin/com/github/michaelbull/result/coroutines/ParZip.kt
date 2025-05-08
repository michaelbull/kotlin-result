package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private typealias Producer<T, E> = suspend CoroutineScope.() -> Result<T, E>

private suspend inline fun <T, E, V> parZipInternal(
    producers: List<Producer<T, E>>,
    crossinline transform: suspend CoroutineScope.(values: List<T>) -> V,
): Result<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return coroutineBinding {
        val values = producers
            .map { producer -> async { producer().bind() } }
            .awaitAll()
        transform(values)
    }
}

/**
 * Runs [producer1] and [producer2] in parallel, combining their successful results with [transform].
 * If either computation fails with an [Err], the other is cancelled, and the error is returned as [Err].
 */
public suspend fun <T1, T2, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    transform: suspend CoroutineScope.(T1, T2) -> V,
): Result<V, E> =
    parZipInternal(listOf(producer1, producer2)) {
        @Suppress("UNCHECKED_CAST")
        transform(it[0] as T1, it[1] as T2)
    }

/**
 * Runs [producer1], [producer2], and [producer3] in parallel, combining their successful results with [transform].
 * If any computation fails with an [Err], the others are cancelled, and the error is returned as [Err].
 */
public suspend fun <T1, T2, T3, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    transform: suspend CoroutineScope.(T1, T2, T3) -> V,
): Result<V, E> =
    parZipInternal(listOf(producer1, producer2, producer3)) {
        @Suppress("UNCHECKED_CAST")
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3
        )
    }

/**
 * Runs [producer1], [producer2], [producer3], and [producer4] in parallel, combining their successful results with [transform].
 * If any computation fails with an [Err], the others are cancelled, and the error is returned as [Err].
 */
public suspend fun <T1, T2, T3, T4, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    producer4: Producer<T4, E>,
    transform: suspend CoroutineScope.(T1, T2, T3, T4) -> V,
): Result<V, E> =
    parZipInternal(listOf(producer1, producer2, producer3, producer4)) {
        @Suppress("UNCHECKED_CAST")
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4
        )
    }

/**
 * Runs [producer1], [producer2], [producer3], [producer4], and [producer5] in parallel, combining their successful results with [transform].
 * If any computation fails with an [Err], the others are cancelled, and the error is returned as [Err].
 */
public suspend fun <T1, T2, T3, T4, T5, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    producer4: Producer<T4, E>,
    producer5: Producer<T5, E>,
    transform: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V,
): Result<V, E> =
    parZipInternal(listOf(producer1, producer2, producer3, producer4, producer5)) {
        @Suppress("UNCHECKED_CAST")
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5
        )
    }
