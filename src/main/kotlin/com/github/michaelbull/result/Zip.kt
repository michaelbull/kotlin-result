package com.github.michaelbull.result

private typealias Producer<T, E> = () -> Result<T, E>

/**
 * Apply a [transformation][transform] to two [Results][Result], if both [Results][Result] are [Ok].
 * If not, the first argument which is an [Err] will propagate through.
 *
 * - Elm: http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map2
 */
inline fun <V1, V2, E, U> zip(
    result1: Producer<V1, E>,
    result2: Producer<V2, E>,
    transform: (V1, V2) -> U
): Result<U, E> {
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
inline fun <V1, V2, V3, E, U> zip(
    result1: Producer<V1, E>,
    result2: Producer<V2, E>,
    result3: Producer<V3, E>,
    transform: (V1, V2, V3) -> U
): Result<U, E> {
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
inline fun <V1, V2, V3, V4, E, U> zip(
    result1: Producer<V1, E>,
    result2: Producer<V2, E>,
    result3: Producer<V3, E>,
    result4: Producer<V4, E>,
    transform: (V1, V2, V3, V4) -> U
): Result<U, E> {
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
inline fun <V1, V2, V3, V4, V5, E, U> zip(
    result1: Producer<V1, E>,
    result2: Producer<V2, E>,
    result3: Producer<V3, E>,
    result4: Producer<V4, E>,
    result5: Producer<V5, E>,
    transform: (V1, V2, V3, V4, V5) -> U
): Result<U, E> {
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
