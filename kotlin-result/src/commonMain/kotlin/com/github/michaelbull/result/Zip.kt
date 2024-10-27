package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private typealias Producer<T, E> = () -> Result<T, E>

/**
 * Applies the given [transform] function to two [Results][Result], returning early with the first
 * [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map2
 */
public inline fun <T1, T2, E, V> zip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    transform: (T1, T2) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return producer1().flatMap { v1 ->
        producer2().map { v2 ->
            transform(v1, v2)
        }
    }
}

/**
 * Applies the given [transform] function to three [Results][Result], returning early with the
 * first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map3
 */
public inline fun <T1, T2, T3, E, V> zip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    transform: (T1, T2, T3) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return producer1().flatMap { v1 ->
        producer2().flatMap { v2 ->
            producer3().map { v3 ->
                transform(v1, v2, v3)
            }
        }
    }
}

/**
 * Applies the given [transform] function to four [Results][Result], returning early with the
 * first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map4
 */
public inline fun <T1, T2, T3, T4, E, V> zip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    producer4: Producer<T4, E>,
    transform: (T1, T2, T3, T4) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return producer1().flatMap { v1 ->
        producer2().flatMap { v2 ->
            producer3().flatMap { v3 ->
                producer4().map { v4 ->
                    transform(v1, v2, v3, v4)
                }
            }
        }
    }
}

/**
 * Applies the given [transform] function to five [Results][Result], returning early with the
 * first [Err] if a transformation fails.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map5
 */
public inline fun <T1, T2, T3, T4, T5, E, V> zip(
    producer1: Producer<T1, E>,
    producer2: Producer<T2, E>,
    producer3: Producer<T3, E>,
    producer4: Producer<T4, E>,
    producer5: Producer<T5, E>,
    transform: (T1, T2, T3, T4, T5) -> V,
): Result<V, E> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer5, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return producer1().flatMap { v1 ->
        producer2().flatMap { v2 ->
            producer3().flatMap { v3 ->
                producer4().flatMap { v4 ->
                    producer5().map { v5 ->
                        transform(v1, v2, v3, v4, v5)
                    }
                }
            }
        }
    }
}

/**
 * Applies the given [transform] function to two [Results][Result], collecting any result that
 * [is an error][Result.isErr] to a [List].
 */
public inline fun <T1, T2, E, V> zipOrAccumulate(
    producer1: () -> Result<T1, E>,
    producer2: () -> Result<T2, E>,
    transform: (T1, T2) -> V,
): Result<V, List<E>> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.EXACTLY_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    val result1 = producer1()
    val result2 = producer2()

    val results = listOf(
        result1,
        result2,
    )

    return if (results.allOk()) {
        val transformed = transform(
            result1.value,
            result2.value,
        )

        Ok(transformed)
    } else {
        Err(results.filterErrors())
    }
}

/**
 * Applies the given [transform] function to three [Results][Result], collecting any result that
 * [is an error][Result.isErr] to a [List].
 */
public inline fun <T1, T2, T3, E, V> zipOrAccumulate(
    producer1: () -> Result<T1, E>,
    producer2: () -> Result<T2, E>,
    producer3: () -> Result<T3, E>,
    transform: (T1, T2, T3) -> V,
): Result<V, List<E>> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer3, InvocationKind.EXACTLY_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    val result1 = producer1()
    val result2 = producer2()
    val result3 = producer3()

    val results = listOf(
        result1,
        result2,
        result3,
    )

    return if (results.allOk()) {
        val transformed = transform(
            result1.value,
            result2.value,
            result3.value
        )

        Ok(transformed)
    } else {
        Err(results.filterErrors())
    }
}

/**
 * Applies the given [transform] function to four [Results][Result], collecting any result that
 * [is an error][Result.isErr] to a [List].
 */
public inline fun <T1, T2, T3, T4, E, V> zipOrAccumulate(
    producer1: () -> Result<T1, E>,
    producer2: () -> Result<T2, E>,
    producer3: () -> Result<T3, E>,
    producer4: () -> Result<T4, E>,
    transform: (T1, T2, T3, T4) -> V,
): Result<V, List<E>> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer3, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer4, InvocationKind.EXACTLY_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    val result1 = producer1()
    val result2 = producer2()
    val result3 = producer3()
    val result4 = producer4()

    val results = listOf(
        result1,
        result2,
        result3,
        result4,
    )

    return if (results.allOk()) {
        val transformed = transform(
            result1.value,
            result2.value,
            result3.value,
            result4.value,
        )

        Ok(transformed)
    } else {
        Err(results.filterErrors())
    }
}

/**
 * Applies the given [transform] function to five [Results][Result], collecting any result that
 * [is an error][Result.isErr] to a [List].
 */
public inline fun <T1, T2, T3, T4, T5, E, V> zipOrAccumulate(
    producer1: () -> Result<T1, E>,
    producer2: () -> Result<T2, E>,
    producer3: () -> Result<T3, E>,
    producer4: () -> Result<T4, E>,
    producer5: () -> Result<T5, E>,
    transform: (T1, T2, T3, T4, T5) -> V,
): Result<V, List<E>> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer3, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer4, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer5, InvocationKind.EXACTLY_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    val result1 = producer1()
    val result2 = producer2()
    val result3 = producer3()
    val result4 = producer4()
    val result5 = producer5()

    val results = listOf(
        result1,
        result2,
        result3,
        result4,
        result5
    )

    return if (results.allOk()) {
        val transformed = transform(
            result1.value,
            result2.value,
            result3.value,
            result4.value,
            result5.value,
        )

        Ok(transformed)
    } else {
        Err(results.filterErrors())
    }
}
