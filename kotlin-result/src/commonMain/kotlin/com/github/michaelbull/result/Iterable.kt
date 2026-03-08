package com.github.michaelbull.result

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
 * Returns a list containing only elements that [are ok][Result.isOk].
 */
public fun <V, E> Iterable<Result<V, E>>.filterOk(): List<V> {
    return filterOkTo(ArrayList())
}

/**
 * Returns a list containing only elements that [are an error][Result.isErr].
 */
public fun <V, E> Iterable<Result<V, E>>.filterErr(): List<E> {
    return filterErrTo(ArrayList())
}

/**
 * Appends the [values][Result.value] of each element that [is ok][Result.isOk] to the given
 * [destination].
 */
public fun <V, E, C : MutableCollection<in V>> Iterable<Result<V, E>>.filterOkTo(destination: C): C {
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
public fun <V, E, C : MutableCollection<in E>> Iterable<Result<V, E>>.filterErrTo(destination: C): C {
    for (element in this) {
        if (element.isErr) {
            destination.add(element.error)
        }
    }

    return destination
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
 * Performs the given [action] on each [ok][Result.isOk] value and returns the collection itself
 * afterwards.
 */
public inline fun <V, E> Iterable<Result<V, E>>.onEachOk(action: (V) -> Unit): Iterable<Result<V, E>> {
    return onEach { result ->
        result.onOk(action)
    }
}

/**
 * Performs the given [action] on each [ok][Result.isOk] value and its index and returns the
 * collection itself afterwards.
 */
public inline fun <V, E> Iterable<Result<V, E>>.onEachOkIndexed(action: (index: Int, V) -> Unit): Iterable<Result<V, E>> {
    return onEachIndexed { index, result ->
        if (result.isOk) {
            action(index, result.value)
        }
    }
}

/**
 * Performs the given [action] on each [error][Result.isErr] value and returns the collection itself
 * afterwards.
 */
public inline fun <V, E> Iterable<Result<V, E>>.onEachErr(action: (E) -> Unit): Iterable<Result<V, E>> {
    return onEach { result ->
        result.onErr(action)
    }
}

/**
 * Performs the given [action] on each [error][Result.isErr] value and its index and returns the
 * collection itself afterwards.
 */
public inline fun <V, E> Iterable<Result<V, E>>.onEachErrIndexed(action: (index: Int, E) -> Unit): Iterable<Result<V, E>> {
    return onEachIndexed { index, result ->
        if (result.isErr) {
            action(index, result.error)
        }
    }
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
 * Combines the specified [results] into a single [Result] (holding a [List]). Elements in the
 * returned list are in the same order as the specified [results].
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 * - Haskell: [Data.Traversable.sequenceA](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:sequenceA)
 */
public fun <V, E, R : Result<V, E>> combine(vararg results: R): Result<List<V>, E> {
    return results.asIterable().combine()
}

/**
 * Combines [this] iterable into a single [Result] (holding a [List]). Elements in the returned
 * list are in the same order as [this].
 *
 * - If all results [are ok][Result.isOk], returns [Ok] with all values.
 * - If any result [is an error][Result.isErr], returns the first [Err] encountered.
 * - If the iterable is empty, returns [Ok] with an empty list.
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 * - Haskell: [Data.Traversable.sequenceA](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:sequenceA)
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
 * Combines [this] iterable into a single [Result], appending all [ok][Result.isOk] values to the
 * given [destination]. Elements in the returned collection are in the same order as [this].
 *
 * - If all results [are ok][Result.isOk], returns [Ok] with the [destination].
 * - If any result [is an error][Result.isErr], returns the first [Err] encountered.
 * - If the iterable is empty, returns [Ok] with the (empty) [destination].
 */
public fun <V, E, C : MutableCollection<in V>> Iterable<Result<V, E>>.combineTo(
    destination: C,
): Result<C, E> {
    val values = mapTo(destination) { result ->
        when {
            result.isOk -> result.value
            else -> return result.asErr()
        }
    }

    return Ok(values)
}

/**
 * Combines the specified [results] into a single [Result] (holding a [List] of errors). Elements
 * in the returned list are in the same order as the specified [results].
 */
public fun <V, E, R : Result<V, E>> combineErr(vararg results: R): Result<V, List<E>> {
    return results.asIterable().combineErr()
}

/**
 * Combines [this] iterable into a single [Result] (holding a [List] of errors). Elements in the
 * returned list are in the same order as [this].
 *
 * - If all results [are errors][Result.isErr], returns [Err] with all errors.
 * - If any result [is ok][Result.isOk], returns the first [Ok] encountered.
 * - If the iterable is empty, returns [Err] with an empty list.
 */
public fun <V, E> Iterable<Result<V, E>>.combineErr(): Result<V, List<E>> {
    val errors = map { result ->
        when {
            result.isErr -> result.error
            else -> return result.asOk()
        }
    }

    return Err(errors)
}

/**
 * Combines [this] iterable into a single [Result], appending all [error][Result.isErr] values to
 * the given [destination]. Elements in the returned collection are in the same order as [this].
 *
 * - If all results [are errors][Result.isErr], returns [Err] with the [destination].
 * - If any result [is ok][Result.isOk], returns the first [Ok] encountered.
 * - If the iterable is empty, returns [Err] with the (empty) [destination].
 */
public fun <V, E, C : MutableCollection<in E>> Iterable<Result<V, E>>.combineErrTo(
    destination: C,
): Result<V, C> {
    val errors = mapTo(destination) { result ->
        when {
            result.isErr -> result.error
            else -> return result.asOk()
        }
    }

    return Err(errors)
}

/**
 * Returns a [List] containing the [error][Result.error] of each element in the specified [results]
 * that [is an error][Result.isErr]. Elements in the returned list are in the same order as the
 * specified [results].
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
public fun <V, E, R : Result<V, E>> errorsOf(vararg results: R): List<E> {
    return results.asIterable().filterErr()
}

/**
 * Returns a [List] containing the [value][Result.value] of each element in the specified [results]
 * that [is ok][Result.isOk]. Elements in the returned list are in the same order as the specified
 * [results].
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
public fun <V, E, R : Result<V, E>> valuesOf(vararg results: R): List<V> {
    return results.asIterable().filterOk()
}
