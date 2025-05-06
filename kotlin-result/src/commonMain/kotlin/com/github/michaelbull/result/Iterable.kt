package com.github.michaelbull.result

/**
 * Returns a list containing only elements that [are ok][Result.isOk].
 */
public fun <V, E> Iterable<Result<V, E>>.filterValues(): List<V> {
    return filterValuesTo(ArrayList())
}

/**
 * Returns a list containing only elements that [are an error][Result.isErr].
 */
public fun <V, E> Iterable<Result<V, E>>.filterErrors(): List<E> {
    return filterErrorsTo(ArrayList())
}

/**
 * Appends the [values][Result.value] of each element that [is ok][Result.isOk] to the given
 * [destination].
 */
public fun <V, E, C : MutableCollection<in V>> Iterable<Result<V, E>>.filterValuesTo(destination: C): C {
    for (element in this) {
        if (element.isOk) {
            destination.add(element.value)
        }
    }

    return destination
}

/**
 * Appends the [errors][Result.error] of each element that [is an error][Result.isErr] to the given
 * [destination].
 */
public fun <V, E, C : MutableCollection<in E>> Iterable<Result<V, E>>.filterErrorsTo(destination: C): C {
    for (element in this) {
        if (element.isErr) {
            destination.add(element.error)
        }
    }

    return destination
}

/**
 * Returns `true` if each element [is ok][Result.isOk], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.allOk(): Boolean {
    return all(Result<V, E>::isOk)
}

/**
 * Returns `true` if each element [is an error][Result.isErr], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.allErr(): Boolean {
    return all(Result<V, E>::isErr)
}

/**
 * Returns `true` if at least one element [is ok][Result.isOk], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.anyOk(): Boolean {
    return any(Result<V, E>::isOk)
}

/**
 * Returns `true` if at least one element [is an error][Result.isErr], `false` otherwise.
 */
public fun <V, E> Iterable<Result<V, E>>.anyErr(): Boolean {
    return any(Result<V, E>::isErr)
}

/**
 * Returns the number of elements that [are ok][Result.isOk].
 */
public fun <V, E> Iterable<Result<V, E>>.countOk(): Int {
    return count(Result<V, E>::isOk)
}

/**
 * Returns the number of elements that [are an error][Result.isErr].
 */
public fun <V, E> Iterable<Result<V, E>>.countErr(): Int {
    return count(Result<V, E>::isErr)
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
        val result = operation(accumulator, element)

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }
    }

    return Ok(accumulator)
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from right to left to
 * each element and current accumulator value.
 */
public inline fun <T, R, E> List<T>.foldRight(
    initial: R,
    operation: (T, acc: R) -> Result<R, E>,
): Result<R, E> {
    var accumulator = initial

    if (!isEmpty()) {
        val iterator = listIterator(size)

        while (iterator.hasPrevious()) {
            val result = operation(iterator.previous(), accumulator)

            accumulator = when {
                result.isOk -> result.value
                else -> return Err(result.error)
            }
        }
    }

    return Ok(accumulator)
}

/**
 * Combines the specified [results] into a single [Result] (holding a [List]). Elements in the
 * returned list are in the same order as the specified [results].
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 */
public fun <V, E, R : Result<V, E>> combine(vararg results: R): Result<List<V>, E> {
    return results.asIterable().combine()
}

/**
 * Combines [this] iterable into a single [Result] (holding a [List]). Elements in the returned
 * list are in the the same order as [this].
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 */
public fun <V, E> Iterable<Result<V, E>>.combine(): Result<List<V>, E> {
    val values = map { result ->
        when {
            result.isOk -> result.value
            else -> return result.asErr()
        }
    }

    return Ok(values)
}

/**
 * Returns a [List] containing the [value][Result.value] of each element in the specified [results]
 * that [is ok][Result.isOk]. Elements in the returned list are in the same order as the specified
 * [results].
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
public fun <V, E, R : Result<V, E>> valuesOf(vararg results: R): List<V> {
    return results.asIterable().filterValues()
}

/**
 * Returns a [List] containing the [error][Result.error] of each element in the specified [results]
 * that [is an error][Result.isErr]. Elements in the returned list are in the same order as the
 * specified [results].
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
public fun <V, E, R : Result<V, E>> errorsOf(vararg results: R): List<E> {
    return results.asIterable().filterErrors()
}

/**
 * Partitions the specified [results] into a [Pair] of [Lists][List]. An element that
 * [is ok][Result.isOk] will appear in the [first][Pair.first] list, whereas an element that
 * [is an error][Result.isErr] will appear in the [second][Pair.second] list.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
public fun <V, E, R : Result<V, E>> partition(vararg results: R): Pair<List<V>, List<E>> {
    return results.asIterable().partition()
}

/**
 *
 * Partitions this into a [Pair] of [Lists][List]. An element that [is ok][Result.isOk] will appear
 * in the [first][Pair.first] list, whereas an element that [is an error][Result.isErr] will appear
 * in the [second][Pair.second] list.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
public fun <V, E> Iterable<Result<V, E>>.partition(): Pair<List<V>, List<E>> {
    val values = mutableListOf<V>()
    val errors = mutableListOf<E>()

    for (result in this) {
        if (result.isOk) {
            values += result.value
        } else {
            errors += result.error
        }
    }

    return Pair(values, errors)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the same order as [this].
 */
public inline fun <V, E, U> Iterable<V>.mapResult(
    transform: (V) -> Result<U, E>,
): Result<List<U>, E> {
    val values = map { element ->
        val transformed = transform(element)

        when {
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
        }
    }

    return Ok(values)
}

/**
 * Applies the given [transform] function to each element of the original collection and appends
 * the results to the given [destination], returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the same order as [this].
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultTo(
    destination: C,
    transform: (V) -> Result<U, E>,
): Result<C, E> {
    val values = mapTo(destination) { element ->
        val transformed = transform(element)

        when {
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
        }
    }

    return Ok(values)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each element in the original collection, returning early with the
 * first [Err] if a transformation fails. Elements in the returned list are in the same order as
 * [this].
 */
public inline fun <V, E, U : Any> Iterable<V>.mapResultNotNull(
    transform: (V) -> Result<U, E>?,
): Result<List<U>, E> {
    val values = mapNotNull { element ->
        val transformed = transform(element)

        when {
            transformed == null -> null
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
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
        val transformed = transform(element)

        when {
            transformed == null -> null
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
        }
    }

    return Ok(values)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element and its index in the original collection, returning early with the
 * first [Err] if a transformation fails. Elements in the returned list are in same order as
 * [this].
 */
public inline fun <V, E, U> Iterable<V>.mapResultIndexed(
    transform: (index: Int, V) -> Result<U, E>,
): Result<List<U>, E> {
    val values = mapIndexed { index, element ->
        val transformed = transform(index, element)

        when {
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
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
        val transformed = transform(index, element)

        when {
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
        }
    }

    return Ok(values)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each element and its index in the original collection, returning
 * early with the first [Err] if a transformation fails. Elements in the returned list are in
 * the same order as [this].
 */
public inline fun <V, E, U : Any> Iterable<V>.mapResultIndexedNotNull(
    transform: (index: Int, V) -> Result<U, E>?,
): Result<List<U>, E> {
    val values = mapIndexedNotNull { index, element ->
        val transformed = transform(index, element)

        when {
            transformed == null -> null
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
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
        val transformed = transform(index, element)

        when {
            transformed == null -> null
            transformed.isOk -> transformed.value
            else -> return transformed.asErr()
        }
    }

    return Ok(values)
}

/**
 * Applies the given [transform] function to each element in this [Iterable<V>], collecting the results into a [Result<List<U>, E>].
 * If all transformations succeed, returns an [Ok] containing a list of [U] values in the same order as the original [Iterable].
 * If any transformation fails, immediately returns the first [Err]
 * encountered and stops processing further elements.
 */
public inline fun <V, E, U> Iterable<V>.traverse(transform: (V) -> Result<U, E>): Result<List<U>, E> {
    return fold(
        initial = emptyList<U>(),
        operation = { acc: List<U>, element: V ->
            transform(element).map { value ->
                acc + value
            }
        }
    )
}
