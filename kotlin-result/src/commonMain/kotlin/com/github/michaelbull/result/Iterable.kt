package com.github.michaelbull.result

/**
 * Returns a list containing only elements that are [Ok].
 */
public fun <V, E> Iterable<Result<V, E>>.filterValues(): List<V> {
    return filterValuesTo(ArrayList())
}

/**
 * Returns a list containing only elements that are [Err].
 */
public fun <V, E> Iterable<Result<V, E>>.filterErrors(): List<E> {
    return filterErrorsTo(ArrayList())
}

/**
 * Appends the [values][Ok.value] of each element that is [Ok] to the given [destination].
 */
public fun <V, E, C : MutableCollection<in V>> Iterable<Result<V, E>>.filterValuesTo(destination: C): C {
    for (element in this) {
        if (element is Ok<V>) {
            destination.add(element.value)
        }
    }

    return destination
}

/**
 * Appends the [errors][Err.error] of each element that is [Err] to the given [destination].
 */
public fun <V, E, C : MutableCollection<in E>> Iterable<Result<V, E>>.filterErrorsTo(destination: C): C {
    for (element in this) {
        if (element is Err<E>) {
            destination.add(element.error)
        }
    }

    return destination
}

/**
 * Returns `true` if each element is [Ok], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.allOk(): Boolean {
    return all { it is Ok }
}

/**
 * Returns `true` if each element is [Err], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.allErr(): Boolean {
    return all { it is Err }
}

/**
 * Returns `true` if at least one element is [Ok], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.anyOk(): Boolean {
    return any { it is Ok }
}

/**
 * Returns `true` if at least one element is [Err], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.anyErr(): Boolean {
    return any { it is Err }
}

/**
 * Returns the number of elements that are [Ok].
 */
public fun <V, E> Iterable<Result<V, E>>.countOk(): Int {
    return count { it is Ok }
}

/**
 * Returns the number of elements that are [Err].
 */
public fun <V, E> Iterable<Result<V, E>>.countErr(): Int {
    return count { it is Err }
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right to
 * current accumulator value and each element.
 */
public inline fun <T, R, E> Iterable<T>.fold(
    initial: R,
    operation: (acc: R, T) -> Result<R, E>,
): Result<R, E> {
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
public inline fun <T, R, E, C : Result<R, E>> List<T>.foldRight(
    initial: R,
    operation: (T, acc: R) -> Result<R, E>,
): Result<R, E> {
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
public fun <V, E, R : Result<V, E>> combine(vararg results: R): Result<List<V>, E> {
    return results.asIterable().combine()
}

/**
 * Combines an [Iterable] of [Results][Result] into a single [Result] (holding a [List]). Elements in the returned
 * list are in the input [Iterable] order.
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 */
public fun <V, E> Iterable<Result<V, E>>.combine(): Result<List<V>, E> {
    val values = map {
        when (it) {
            is Ok -> it.value
            is Err -> return it
        }
    }

    return Ok(values)
}

/**
 * Extracts from a vararg of [Results][Result] all the [Ok] elements. All the [Ok] elements are
 * extracted in order.
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
public fun <V, E, R : Result<V, E>> getAll(vararg results: R): List<V> {
    return results.asIterable().filterValues()
}

/**
 * Extracts from an [Iterable] of [Results][Result] all the [Ok] elements. All the [Ok] elements
 * are extracted in order.
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
@Deprecated(
    message = "Use filterValues instead",
    replaceWith = ReplaceWith("filterValues()")
)
public fun <V, E> Iterable<Result<V, E>>.getAll(): List<V> {
    return filterValues()
}

/**
 * Extracts from a vararg of [Results][Result] all the [Err] elements. All the [Err] elements are
 * extracted in order.
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
public fun <V, E, R : Result<V, E>> getAllErrors(vararg results: R): List<E> {
    return results.asIterable().filterErrors()
}

/**
 * Extracts from an [Iterable] of [Results][Result] all the [Err] elements. All the [Err] elements
 * are extracted in order.
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
@Deprecated(
    message = "Use filterErrors instead",
    replaceWith = ReplaceWith("filterErrors()")
)
public fun <V, E> Iterable<Result<V, E>>.getAllErrors(): List<E> {
    return filterErrors()
}

/**
 * Partitions a vararg of [Results][Result] into a [Pair] of [Lists][List]. All the [Ok] elements
 * are extracted, in order, to the [first][Pair.first] value. Similarly the [Err] elements are
 * extracted to the [Pair.second] value.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
public fun <V, E, R : Result<V, E>> partition(vararg results: R): Pair<List<V>, List<E>> {
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
    transform: (V) -> Result<U, E>,
): Result<List<U>, E> {
    val values = map { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    }

    return Ok(values)
}

/**
 * Applies the given [transform] function to each element of the original collection and appends
 * the results to the given [destination], returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the input [Iterable] order.
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultTo(
    destination: C,
    transform: (V) -> Result<U, E>,
): Result<C, E> {
    val values = mapTo(destination) { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    }

    return Ok(values)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each element in the original collection, returning early with the
 * first [Err] if a transformation fails. Elements in the returned list are in the input [Iterable]
 * order.
 */
public inline fun <V, E, U : Any> Iterable<V>.mapResultNotNull(
    transform: (V) -> Result<U, E>?,
): Result<List<U>, E> {
    val values = mapNotNull { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    }

    return Ok(values)
}

/**
 * Applies the given [transform] function to each element in the original collection and appends
 * only the non-null results to the given [destination], returning early with the first [Err] if a
 * transformation fails.
 */
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.mapResultNotNullTo(
    destination: C,
    transform: (V) -> Result<U, E>?,
): Result<C, E> {
    val values = mapNotNullTo(destination) { element ->
        when (val transformed = transform(element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    }

    return Ok(values)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element and its index in the original collection, returning early with the
 * first [Err] if a transformation fails. Elements in the returned list are in the input [Iterable]
 * order.
 */
public inline fun <V, E, U> Iterable<V>.mapResultIndexed(
    transform: (index: Int, V) -> Result<U, E>,
): Result<List<U>, E> {
    val values = mapIndexed { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    }

    return Ok(values)
}

/**
 * Applies the given [transform] function to each element and its index in the original collection
 * and appends the results to the given [destination], returning early with the first [Err] if a
 * transformation fails.
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultIndexedTo(
    destination: C,
    transform: (index: Int, V) -> Result<U, E>,
): Result<C, E> {
    val values = mapIndexedTo(destination) { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
        }
    }

    return Ok(values)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each element and its index in the original collection, returning
 * early with the first [Err] if a transformation fails. Elements in the returned list are in
 * the input [Iterable] order.
 */
public inline fun <V, E, U : Any> Iterable<V>.mapResultIndexedNotNull(
    transform: (index: Int, V) -> Result<U, E>?,
): Result<List<U>, E> {
    val values = mapIndexedNotNull { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    }

    return Ok(values)
}

/**
 * Applies the given [transform] function to each element and its index in the original collection
 * and appends only the non-null results to the given [destination], returning early with the first
 * [Err] if a transformation fails.
 */
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.mapResultIndexedNotNullTo(
    destination: C,
    transform: (index: Int, V) -> Result<U, E>?,
): Result<C, E> {
    val values = mapIndexedNotNullTo(destination) { index, element ->
        when (val transformed = transform(index, element)) {
            is Ok -> transformed.value
            is Err -> return transformed
            null -> null
        }
    }

    return Ok(values)
}
