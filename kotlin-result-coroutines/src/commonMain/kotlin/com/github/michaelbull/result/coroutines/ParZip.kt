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
    vararg producers: Producer<T, E>,
    crossinline transform: suspend CoroutineScope.(values: List<T>) -> V,
): Result<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return coroutineBinding {
        val values = producers.map { producer ->
            async {
                producer().bind()
            }
        }

        transform(values.awaitAll())
    }
}

/**
 * Applies the given [transform] function to two [Results][Result] _in parallel_, returning early
 * with the first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map2
 */
public suspend fun <T1, T2, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    transform: suspend CoroutineScope.(T1, T2) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return parZipInternal(producer1, producer2) { values ->
        @Suppress("UNCHECKED_CAST")
        transform(
            values[0] as T1,
            values[1] as T2,
        )
    }
}

/**
 * Applies the given [transform] function to three [Results][Result] _in parallel_, returning early
 * with the first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map3
 */
public suspend fun <T1, T2, T3, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    transform: suspend CoroutineScope.(T1, T2, T3) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return parZipInternal(producer1, producer2, producer3) { values ->
        @Suppress("UNCHECKED_CAST")
        transform(
            values[0] as T1,
            values[1] as T2,
            values[2] as T3,
        )
    }
}

/**
 * Applies the given [transform] function to four [Results][Result] _in parallel_, returning early
 * with the first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map4
 */
public suspend fun <T1, T2, T3, T4, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    producer4: Producer<T4, E>,
    transform: suspend CoroutineScope.(T1, T2, T3, T4) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return parZipInternal(producer1, producer2, producer3, producer4) { values ->
        @Suppress("UNCHECKED_CAST")
        transform(
            values[0] as T1,
            values[1] as T2,
            values[2] as T3,
            values[3] as T4,
        )
    }
}

/**
 * Applies the given [transform] function to five [Results][Result] _in parallel_, returning early
 * with the first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map5
 */
public suspend fun <T1, T2, T3, T4, T5, E, V> parZip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    producer4: Producer<T4, E>,
    producer5: Producer<T5, E>,
    transform: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer5, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return parZipInternal(producer1, producer2, producer3, producer4, producer5) { values ->
        @Suppress("UNCHECKED_CAST")
        transform(
            values[0] as T1,
            values[1] as T2,
            values[2] as T3,
            values[3] as T4,
            values[4] as T5,
        )
    }
}
