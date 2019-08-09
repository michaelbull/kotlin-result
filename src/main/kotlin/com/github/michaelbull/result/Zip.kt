package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private typealias Producer<T, E> = () -> Result<T, E>

/**
 * Apply a [transformation][transform] to two [Results][Result], if both [Results][Result] are [Ok].
 * If not, the first argument which is an [Err] will propagate through.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map2
 */
inline fun <T1, T2, E, V> zip(
    result1: Producer<T1, E>,
    result2: Producer<T2, E>,
    transform: (T1, T2) -> V
): Result<V, E> {
    contract {
        callsInPlace(result1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(result2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return result1().flatMap { v1 ->
        result2().map { v2 ->
            transform(v1, v2)
        }
    }
}

/**
 * Apply a [transformation][transform] to three [Results][Result], if all [Results][Result] are [Ok].
 * If not, the first argument which is an [Err] will propagate through.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map3
 */
inline fun <T1, T2, T3, E, V> zip(
    result1: Producer<T1, E>,
    result2: Producer<T2, E>,
    result3: Producer<T3, E>,
    transform: (T1, T2, T3) -> V
): Result<V, E> {
    contract {
        callsInPlace(result1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(result2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(result3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return result1().flatMap { v1 ->
        result2().flatMap { v2 ->
            result3().map { v3 ->
                transform(v1, v2, v3)
            }
        }
    }
}

/**
 * Apply a [transformation][transform] to four [Results][Result], if all [Results][Result] are [Ok].
 * If not, the first argument which is an [Err] will propagate through.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map4
 */
inline fun <T1, T2, T3, T4, E, V> zip(
    result1: Producer<T1, E>,
    result2: Producer<T2, E>,
    result3: Producer<T3, E>,
    result4: Producer<T4, E>,
    transform: (T1, T2, T3, T4) -> V
): Result<V, E> {
    contract {
        callsInPlace(result1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(result2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(result3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(result4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return result1().flatMap { v1 ->
        result2().flatMap { v2 ->
            result3().flatMap { v3 ->
                result4().map { v4 ->
                    transform(v1, v2, v3, v4)
                }
            }
        }
    }
}

/**
 * Apply a [transformation][transform] to five [Results][Result], if all [Results][Result] are [Ok].
 * If not, the first argument which is an [Err] will propagate through.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map5
 */
inline fun <T1, T2, T3, T4, T5, E, V> zip(
    result1: Producer<T1, E>,
    result2: Producer<T2, E>,
    result3: Producer<T3, E>,
    result4: Producer<T4, E>,
    result5: Producer<T5, E>,
    transform: (T1, T2, T3, T4, T5) -> V
): Result<V, E> {
    contract {
        callsInPlace(result1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(result2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(result3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(result4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(result5, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return result1().flatMap { v1 ->
        result2().flatMap { v2 ->
            result3().flatMap { v3 ->
                result4().flatMap { v4 ->
                    result5().map { v5 ->
                        transform(v1, v2, v3, v4, v5)
                    }
                }
            }
        }
    }
}
