package com.github.michaelbull.result

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right to
 * current accumulator value and each element.
 */
public inline fun <T, R, E> Iterable<T>.fold(initial: R, operation: (acc: R, T) -> Result<R, E>): Result<R, E> {
    var accumulator = initial

    for (element in this) {
        accumulator = when (val result = operation(accumulator, element)) {
            is Ok -> result.value
            is Err -> return Err(result.error)
        }
    }

    return Ok(accumulator)
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from right to left to
 * each element and current accumulator value.
 */
public inline fun <T, R, E> List<T>.foldRight(initial: R, operation: (T, acc: R) -> Result<R, E>): Result<R, E> {
    var accumulator = initial

    if (!isEmpty()) {
        val iterator = listIterator(size)
        while (iterator.hasPrevious()) {
            accumulator = when (val result = operation(iterator.previous(), accumulator)) {
                is Ok -> result.value
                is Err -> return Err(result.error)
            }
        }
    }

    return Ok(accumulator)
}

/**
 * Combines a vararg of [Results][Result] into a single [Result] (holding a [List]). Elements in the returned list
 * are in the same order is the input vararg.
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 */
public fun <V, E> combine(vararg results: Result<V, E>): Result<List<V>, E> {
    return results.asIterable().combine()
}

/**
 * Combines an [Iterable] of [Results][Result] into a single [Result] (holding a [List]). Elements in the returned
 * list are in the input [Iterable] order.
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 */
public fun <V, E> Iterable<Result<V, E>>.combine(): Result<List<V>, E> {
    return Ok(map {
        when (it) {
            is Ok -> it.value
            is Err -> return it
        }
    })
}

/**
 * Extracts from a vararg of [Results][Result] all the [Ok] elements. All the [Ok] elements are
 * extracted in order.
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
public fun <V, E> getAll(vararg results: Result<V, E>): List<V> {
    return results.asIterable().getAll()
}

/**
 * Extracts from an [Iterable] of [Results][Result] all the [Ok] elements. All the [Ok] elements
 * are extracted in order.
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
public fun <V, E> Iterable<Result<V, E>>.getAll(): List<V> {
    return filterIsInstance<Ok<V>>().map { it.value }
}

/**
 * Extracts from a vararg of [Results][Result] all the [Err] elements. All the [Err] elements are
 * extracted in order.
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
public fun <V, E> getAllErrors(vararg results: Result<V, E>): List<E> = results.asIterable().getAllErrors()

/**
 * Extracts from an [Iterable] of [Results][Result] all the [Err] elements. All the [Err] elements
 * are extracted in order.
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
public fun <V, E> Iterable<Result<V, E>>.getAllErrors(): List<E> {
    return filterIsInstance<Err<E>>().map { it.error }
}

/**
 * Partitions a vararg of [Results][Result] into a [Pair] of [Lists][List]. All the [Ok] elements
 * are extracted, in order, to the [first][Pair.first] value. Similarly the [Err] elements are
 * extracted to the [Pair.second] value.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
public fun <V, E> partition(vararg results: Result<V, E>): Pair<List<V>, List<E>> {
    return results.asIterable().partition()
}

/**
 * Partitions an [Iterable] of [Results][Result] into a [Pair] of  [Lists][List]. All the [Ok]
 * elements are extracted, in order, to the [first][Pair.first] value. Similarly the [Err] elements
 * are extracted to the [Pair.second] value.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
public fun <V, E> Iterable<Result<V, E>>.partition(): Pair<List<V>, List<E>> {
    val values = mutableListOf<V>()
    val errors = mutableListOf<E>()

    forEach { result ->
        when (result) {
            is Ok -> values.add(result.value)
            is Err -> errors.add(result.error)
        }
    }

    return Pair(values, errors)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the input [Iterable] order.
 */
public inline fun <V, E, U> Iterable<V>.mapResult(
    transform: (V) -> Result<U, E>
): Result<List<U>, E> {
    return Ok(map { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    })
}

/**
 * Applies the given [transform] function to each element of the original collection and appends
 * the results to the given [destination], returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the input [Iterable] order.
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultTo(
    destination: C,
    transform: (V) -> Result<U, E>
): Result<C, E> {
    return Ok(mapTo(destination) { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    })
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each element in the original collection, returning early with the
 * first [Err] if a transformation fails. Elements in the returned list are in the input [Iterable]
 * order.
 */
public inline fun <V, E, U : Any> Iterable<V>.mapResultNotNull(
    transform: (V) -> Result<U, E>?
): Result<List<U>, E> {
    return Ok(mapNotNull { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    })
}

/**
 * Applies the given [transform] function to each element in the original collection and appends
 * only the non-null results to the given [destination], returning early with the first [Err] if a
 * transformation fails.
 */
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.mapResultNotNullTo(
    destination: C,
    transform: (V) -> Result<U, E>?
): Result<C, E> {
    return Ok(mapNotNullTo(destination) { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    })
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element and its index in the original collection, returning early with the
 * first [Err] if a transformation fails. Elements in the returned list are in the input [Iterable]
 * order.
 */
public inline fun <V, E, U> Iterable<V>.mapResultIndexed(
    transform: (index: Int, V) -> Result<U, E>
): Result<List<U>, E> {
    return Ok(mapIndexed { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    })
}

/**
 * Applies the given [transform] function to each element and its index in the original collection
 * and appends the results to the given [destination], returning early with the first [Err] if a
 * transformation fails.
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultIndexedTo(
    destination: C,
    transform: (index: Int, V) -> Result<U, E>
): Result<C, E> {
    return Ok(mapIndexedTo(destination) { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    })
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each element and its index in the original collection, returning
 * early with the first [Err] if a transformation fails. Elements in the returned list are in
 * the input [Iterable] order.
 */
public inline fun <V, E, U : Any> Iterable<V>.mapResultIndexedNotNull(
    transform: (index: Int, V) -> Result<U, E>?
): Result<List<U>, E> {
    return Ok(mapIndexedNotNull { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    })
}

/**
 * Applies the given [transform] function to each element and its index in the original collection
 * and appends only the non-null results to the given [destination], returning early with the first
 * [Err] if a transformation fails.
 */
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.mapResultIndexedNotNullTo(
    destination: C,
    transform: (index: Int, V) -> Result<U, E>?
): Result<C, E> {
    return Ok(mapIndexedNotNullTo(destination) { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    })
}
