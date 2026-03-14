package com.github.michaelbull.result

@PublishedApi
internal fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int {
    return if (this is Collection<*>) size else default
}

/**
 * Returns the first element for which the fallible [predicate] returns [Ok]`(true)`, returning
 * early with the first [Err] if the [predicate] fails. Returns `null` if no matching element is
 * found.
 *
 * - Rust: [Iterator::try_find](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_find)
 */
public inline fun <T, E> Iterable<T>.tryFind(
    predicate: (T) -> Result<Boolean, E>,
): Result<T, E>? {
    for (element in this) {
        val result = predicate(element)

        when {
            result.isErr -> return Err(result.error)
            result.value -> return Ok(element)
        }
    }

    return null
}

/**
 * Returns the last element for which the fallible [predicate] returns [Ok]`(true)`, returning
 * early with the first [Err] if the [predicate] fails. Returns `null` if no matching element is
 * found.
 *
 * - Rust: [Iterator::try_find](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_find)
 */
public inline fun <T, E> Iterable<T>.tryFindLast(
    predicate: (T) -> Result<Boolean, E>,
): Result<T, E>? {
    var last: T? = null
    var found = false

    for (element in this) {
        val result = predicate(element)

        when {
            result.isErr -> return Err(result.error)
            result.value -> {
                last = element
                found = true
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    return if (found) {
        Ok(last as T)
    } else {
        null
    }
}

/**
 * Returns a [Result] containing a list of elements for which the fallible [predicate] returns
 * [Ok]`(true)`, returning early with the first [Err] if the [predicate] fails.
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public inline fun <T, E> Iterable<T>.tryFilter(
    predicate: (T) -> Result<Boolean, E>,
): Result<List<T>, E> {
    return tryFilterTo(ArrayList(), predicate)
}

/**
 * Appends elements for which the fallible [predicate] returns [Ok]`(true)` to the given
 * [destination], returning early with the first [Err] if the [predicate] fails.
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public inline fun <T, E, C : MutableCollection<in T>> Iterable<T>.tryFilterTo(
    destination: C,
    predicate: (T) -> Result<Boolean, E>,
): Result<C, E> {
    for (element in this) {
        val result = predicate(element)

        when {
            result.isErr -> return Err(result.error)
            result.value -> destination.add(element)
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a list of elements for which the fallible [predicate] returns
 * [Ok]`(false)`, returning early with the first [Err] if the [predicate] fails.
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public inline fun <T, E> Iterable<T>.tryFilterNot(
    predicate: (T) -> Result<Boolean, E>,
): Result<List<T>, E> {
    return tryFilterNotTo(ArrayList(), predicate)
}

/**
 * Appends elements for which the fallible [predicate] returns [Ok]`(false)` to the given
 * [destination], returning early with the first [Err] if the [predicate] fails.
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public inline fun <T, E, C : MutableCollection<in T>> Iterable<T>.tryFilterNotTo(
    destination: C,
    predicate: (T) -> Result<Boolean, E>,
): Result<C, E> {
    for (element in this) {
        val result = predicate(element)

        when {
            result.isErr -> return Err(result.error)
            !result.value -> destination.add(element)
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a list of elements for which the fallible [predicate] returns
 * [Ok]`(true)`, returning early with the first [Err] if the [predicate] fails. The [predicate]
 * receives the index of the element being processed.
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public inline fun <T, E> Iterable<T>.tryFilterIndexed(
    predicate: (index: Int, T) -> Result<Boolean, E>,
): Result<List<T>, E> {
    return tryFilterIndexedTo(ArrayList(), predicate)
}

/**
 * Appends elements for which the fallible [predicate] returns [Ok]`(true)` to the given
 * [destination], returning early with the first [Err] if the [predicate] fails. The [predicate]
 * receives the index of the element being processed.
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public inline fun <T, E, C : MutableCollection<in T>> Iterable<T>.tryFilterIndexedTo(
    destination: C,
    predicate: (index: Int, T) -> Result<Boolean, E>,
): Result<C, E> {
    var index = 0

    for (element in this) {
        val result = predicate(index++, element)

        when {
            result.isErr -> return Err(result.error)
            result.value -> destination.add(element)
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a [Map] of key-value pairs provided by the fallible [transform]
 * function applied to elements of [this], returning early with the first [Err] if the [transform]
 * fails.
 */
public inline fun <T, K, V, E> Iterable<T>.tryAssociate(
    transform: (T) -> Result<Pair<K, V>, E>,
): Result<Map<K, V>, E> {
    return tryAssociateTo(LinkedHashMap(), transform)
}

/**
 * Populates and returns the [destination] mutable map with key-value pairs provided by the
 * fallible [transform] function applied to each element of [this], returning early with the
 * first [Err] if the [transform] fails.
 */
public inline fun <T, K, V, E, M : MutableMap<in K, in V>> Iterable<T>.tryAssociateTo(
    destination: M,
    transform: (T) -> Result<Pair<K, V>, E>,
): Result<M, E> {
    for (element in this) {
        val result = transform(element)

        if (result.isErr) {
            return Err(result.error)
        } else {
            val (key, value) = result.value
            destination[key] = value
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function applied to each element of [this], and values are the elements themselves, returning
 * early with the first [Err] if the [keySelector] fails.
 */
public inline fun <T, K, E> Iterable<T>.tryAssociateBy(
    keySelector: (T) -> Result<K, E>,
): Result<Map<K, T>, E> {
    return tryAssociateByTo(LinkedHashMap(), keySelector)
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function and values are provided by the fallible [valueTransform] function, both applied to
 * each element of [this], returning early with the first [Err] if either function fails.
 */
public inline fun <T, K, V, E> Iterable<T>.tryAssociateBy(
    keySelector: (T) -> Result<K, E>,
    valueTransform: (T) -> Result<V, E>,
): Result<Map<K, V>, E> {
    return tryAssociateByTo(LinkedHashMap(), keySelector, valueTransform)
}

/**
 * Populates and returns the [destination] mutable map with key-value pairs, where keys are
 * provided by the fallible [keySelector] function applied to each element of [this], and values
 * are the elements themselves, returning early with the first [Err] if the [keySelector] fails.
 */
public inline fun <T, K, E, M : MutableMap<in K, in T>> Iterable<T>.tryAssociateByTo(
    destination: M,
    keySelector: (T) -> Result<K, E>,
): Result<M, E> {
    for (element in this) {
        val keyResult = keySelector(element)

        if (keyResult.isErr) {
            return Err(keyResult.error)
        } else {
            destination[keyResult.value] = element
        }
    }

    return Ok(destination)
}

/**
 * Populates and returns the [destination] mutable map with key-value pairs, where keys are
 * provided by the fallible [keySelector] function and values are provided by the fallible
 * [valueTransform] function, both applied to each element of [this], returning early with the
 * first [Err] if either function fails.
 */
public inline fun <T, K, V, E, M : MutableMap<in K, in V>> Iterable<T>.tryAssociateByTo(
    destination: M,
    keySelector: (T) -> Result<K, E>,
    valueTransform: (T) -> Result<V, E>,
): Result<M, E> {
    for (element in this) {
        val keyResult = keySelector(element)

        if (keyResult.isErr) {
            return Err(keyResult.error)
        }

        val valueResult = valueTransform(element)

        if (valueResult.isErr) {
            return Err(valueResult.error)
        }

        destination[keyResult.value] = valueResult.value
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a [Map] where keys are elements of [this] and values are
 * provided by the fallible [valueSelector] function applied to each element, returning early
 * with the first [Err] if the [valueSelector] fails.
 */
public inline fun <K, V, E> Iterable<K>.tryAssociateWith(
    valueSelector: (K) -> Result<V, E>,
): Result<Map<K, V>, E> {
    return tryAssociateWithTo(LinkedHashMap(), valueSelector)
}

/**
 * Populates and returns the [destination] mutable map with key-value pairs for each element of
 * [this], where the key is the element itself and the value is provided by the fallible
 * [valueSelector] function, returning early with the first [Err] if the [valueSelector] fails.
 */
public inline fun <K, V, E, M : MutableMap<in K, in V>> Iterable<K>.tryAssociateWithTo(
    destination: M,
    valueSelector: (K) -> Result<V, E>,
): Result<M, E> {
    for (element in this) {
        val result = valueSelector(element)

        if (result.isErr) {
            return Err(result.error)
        } else {
            destination[element] = result.value
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a single list of all elements yielded from the fallible
 * [transform] function being invoked on each element of [this], returning early with the first
 * [Err] if the [transform] fails.
 */
public inline fun <T, U, E> Iterable<T>.tryFlatMap(
    transform: (T) -> Result<Iterable<U>, E>,
): Result<List<U>, E> {
    return tryFlatMapTo(ArrayList(), transform)
}

/**
 * Appends all elements yielded from the fallible [transform] function being invoked on each
 * element of [this] to the given [destination], returning early with the first [Err] if the
 * [transform] fails.
 */
public inline fun <T, U, E, C : MutableCollection<in U>> Iterable<T>.tryFlatMapTo(
    destination: C,
    transform: (T) -> Result<Iterable<U>, E>,
): Result<C, E> {
    for (element in this) {
        val result = transform(element)

        if (result.isErr) {
            return Err(result.error)
        } else {
            destination.addAll(result.value)
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a single list of all elements yielded from the fallible
 * [transform] function being invoked on each element and its index of [this], returning early
 * with the first [Err] if the [transform] fails.
 */
public inline fun <T, U, E> Iterable<T>.tryFlatMapIndexed(
    transform: (index: Int, T) -> Result<Iterable<U>, E>,
): Result<List<U>, E> {
    return tryFlatMapIndexedTo(ArrayList(), transform)
}

/**
 * Appends all elements yielded from the fallible [transform] function being invoked on each
 * element and its index of [this] to the given [destination], returning early with the first
 * [Err] if the [transform] fails.
 */
public inline fun <T, U, E, C : MutableCollection<in U>> Iterable<T>.tryFlatMapIndexedTo(
    destination: C,
    transform: (index: Int, T) -> Result<Iterable<U>, E>,
): Result<C, E> {
    var index = 0

    for (element in this) {
        val result = transform(index++, element)

        if (result.isErr) {
            return Err(result.error)
        } else {
            destination.addAll(result.value)
        }
    }

    return Ok(destination)
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function applied to each element of [this], and values are lists of elements corresponding to
 * each key, returning early with the first [Err] if the [keySelector] fails.
 */
public inline fun <T, K, E> Iterable<T>.tryGroupBy(
    keySelector: (T) -> Result<K, E>,
): Result<Map<K, List<T>>, E> {
    return tryGroupByTo(LinkedHashMap(), keySelector)
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function and values are lists of results of the fallible [valueTransform] function, both
 * applied to each element of [this], returning early with the first [Err] if either function
 * fails.
 */
public inline fun <T, K, V, E> Iterable<T>.tryGroupBy(
    keySelector: (T) -> Result<K, E>,
    valueTransform: (T) -> Result<V, E>,
): Result<Map<K, List<V>>, E> {
    return tryGroupByTo(LinkedHashMap(), keySelector, valueTransform)
}

/**
 * Populates the [destination] map by grouping elements of [this] by the key returned from the
 * fallible [keySelector] function applied to each element, returning early with the first [Err]
 * if the [keySelector] fails.
 */
public inline fun <T, K, E, M : MutableMap<in K, MutableList<T>>> Iterable<T>.tryGroupByTo(
    destination: M,
    keySelector: (T) -> Result<K, E>,
): Result<M, E> {
    for (element in this) {
        val keyResult = keySelector(element)

        if (keyResult.isErr) {
            return Err(keyResult.error)
        }

        val list = destination.getOrPut(keyResult.value) { ArrayList() }
        list.add(element)
    }

    return Ok(destination)
}

/**
 * Populates the [destination] map by grouping elements of [this] by the key returned from the
 * fallible [keySelector] function and transforming values with the fallible [valueTransform]
 * function, returning early with the first [Err] if either function fails.
 */
public inline fun <T, K, V, E, M : MutableMap<in K, MutableList<V>>> Iterable<T>.tryGroupByTo(
    destination: M,
    keySelector: (T) -> Result<K, E>,
    valueTransform: (T) -> Result<V, E>,
): Result<M, E> {
    for (element in this) {
        val keyResult = keySelector(element)

        if (keyResult.isErr) {
            return Err(keyResult.error)
        }

        val valueResult = valueTransform(element)

        if (valueResult.isErr) {
            return Err(valueResult.error)
        }

        val list = destination.getOrPut(keyResult.value) { ArrayList() }
        list.add(valueResult.value)
    }

    return Ok(destination)
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the same order as [this].
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U> Iterable<V>.tryMap(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.tryMapTo(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U : Any> Iterable<V>.tryMapNotNull(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.tryMapNotNullTo(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U> Iterable<V>.tryMapIndexed(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.tryMapIndexedTo(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U : Any> Iterable<V>.tryMapIndexedNotNull(
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
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.tryMapIndexedNotNullTo(
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
 * Accumulates value starting with [initial] value and applying [operation] from left to right to
 * current accumulator value and each element.
 *
 * - Gleam: [list.try_fold](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_fold)
 * - Haskell: [Control.Monad.foldM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:foldM)
 * - Rust: [Iterator::try_fold](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_fold)
 */
public inline fun <T, R, E> Iterable<T>.tryFold(
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
 *
 * - Gleam: [list.try_fold](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_fold)
 * - Haskell: [Control.Monad.foldM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:foldM)
 * - Rust: [Iterator::try_fold](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_fold)
 */
public inline fun <T, R, E> List<T>.tryFoldRight(
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
 * Performs the given fallible [action] on each element, returning early with the first [Err] if
 * an [action] fails.
 *
 * - Gleam: [list.try_each](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_each)
 * - Rust: [Iterator::try_for_each](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_for_each)
 */
public inline fun <V, E> Iterable<V>.tryForEach(
    action: (V) -> Result<*, E>,
): Result<Unit, E> {
    for (element in this) {
        val result = action(element)

        if (result.isErr) {
            return Err(result.error)
        }
    }

    return Ok(Unit)
}

/**
 * Performs the given fallible [action] on each element and its index, returning early with the
 * first [Err] if an [action] fails.
 *
 * - Gleam: [list.try_each](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_each)
 * - Rust: [Iterator::try_for_each](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_for_each)
 */
public inline fun <V, E> Iterable<V>.tryForEachIndexed(
    action: (index: Int, V) -> Result<*, E>,
): Result<Unit, E> {
    var index = 0

    for (element in this) {
        val result = action(index++, element)

        if (result.isErr) {
            return Err(result.error)
        }
    }

    return Ok(Unit)
}

/**
 * Accumulates value starting with the first element and applying [operation] from left to right to
 * current accumulator value and each element, returning early with the first [Err] if an
 * [operation] fails. Returns `null` if the iterable is empty.
 *
 * - Rust: [Iterator::try_reduce](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_reduce)
 */
public inline fun <T, E> Iterable<T>.tryReduce(
    operation: (acc: T, T) -> Result<T, E>,
): Result<T, E>? {
    val iterator = iterator()

    if (!iterator.hasNext()) {
        return null
    }

    var accumulator = iterator.next()

    while (iterator.hasNext()) {
        val result = operation(accumulator, iterator.next())

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }
    }

    return Ok(accumulator)
}

/**
 * Accumulates value starting with the first element and applying [operation] from left to right to
 * current accumulator value, each element, and its index, returning early with the first [Err] if
 * an [operation] fails. Returns `null` if the iterable is empty.
 *
 * The [operation] receives the index of the current element being processed, starting at 1 (the
 * first element at index 0 is used as the initial accumulator).
 *
 * - Rust: [Iterator::try_reduce](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_reduce)
 */
public inline fun <T, E> Iterable<T>.tryReduceIndexed(
    operation: (index: Int, acc: T, T) -> Result<T, E>,
): Result<T, E>? {
    val iterator = iterator()

    if (!iterator.hasNext()) {
        return null
    }

    var index = 1
    var accumulator = iterator.next()

    while (iterator.hasNext()) {
        val result = operation(index++, accumulator, iterator.next())

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }
    }

    return Ok(accumulator)
}

/**
 * Returns a [Result] containing a list of successive accumulation values generated by applying
 * the fallible [operation] from left to right to each element and current accumulator value that
 * starts with [initial] value, returning early with the first [Err] if an [operation] fails.
 */
public inline fun <T, R, E> Iterable<T>.tryRunningFold(
    initial: R,
    operation: (acc: R, T) -> Result<R, E>,
): Result<List<R>, E> {
    val estimatedSize = collectionSizeOrDefault(9)

    if (estimatedSize == 0) {
        return Ok(listOf(initial))
    }

    val list = ArrayList<R>(estimatedSize + 1).apply { add(initial) }
    var accumulator = initial

    for (element in this) {
        val result = operation(accumulator, element)

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }

        list.add(accumulator)
    }

    return Ok(list)
}

/**
 * Returns a [Result] containing a list of successive accumulation values generated by applying
 * the fallible [operation] from left to right to each element, its index in the original
 * collection, and current accumulator value that starts with [initial] value, returning early
 * with the first [Err] if an [operation] fails.
 */
public inline fun <T, R, E> Iterable<T>.tryRunningFoldIndexed(
    initial: R,
    operation: (index: Int, acc: R, T) -> Result<R, E>,
): Result<List<R>, E> {
    val estimatedSize = collectionSizeOrDefault(9)

    if (estimatedSize == 0) {
        return Ok(listOf(initial))
    }

    val list = ArrayList<R>(estimatedSize + 1).apply { add(initial) }
    var index = 0
    var accumulator = initial

    for (element in this) {
        val result = operation(index++, accumulator, element)

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }

        list.add(accumulator)
    }

    return Ok(list)
}

/**
 * Returns a [Result] containing a list of successive accumulation values generated by applying
 * the fallible [operation] from left to right to each element and current accumulator value that
 * starts with the first element of this collection, returning early with the first [Err] if an
 * [operation] fails.
 */
public inline fun <S, T : S, E> Iterable<T>.tryRunningReduce(
    operation: (acc: S, T) -> Result<S, E>,
): Result<List<S>, E> {
    val iterator = iterator()

    if (!iterator.hasNext()) {
        return Ok(emptyList())
    }

    var accumulator: S = iterator.next()
    val list = ArrayList<S>(collectionSizeOrDefault(10)).apply { add(accumulator) }

    while (iterator.hasNext()) {
        val result = operation(accumulator, iterator.next())

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }

        list.add(accumulator)
    }

    return Ok(list)
}

/**
 * Returns a [Result] containing a list of successive accumulation values generated by applying
 * the fallible [operation] from left to right to each element, its index in the original
 * collection, and current accumulator value that starts with the first element of this
 * collection, returning early with the first [Err] if an [operation] fails.
 *
 * The [operation] receives the index of the current element being processed, starting at 1 (the
 * first element at index 0 is used as the initial accumulator).
 */
public inline fun <S, T : S, E> Iterable<T>.tryRunningReduceIndexed(
    operation: (index: Int, acc: S, T) -> Result<S, E>,
): Result<List<S>, E> {
    val iterator = iterator()

    if (!iterator.hasNext()) {
        return Ok(emptyList())
    }

    var index = 1
    var accumulator: S = iterator.next()
    val list = ArrayList<S>(collectionSizeOrDefault(10)).apply { add(accumulator) }

    while (iterator.hasNext()) {
        val result = operation(index++, accumulator, iterator.next())

        accumulator = when {
            result.isOk -> result.value
            else -> return Err(result.error)
        }

        list.add(accumulator)
    }

    return Ok(list)
}

/**
 * Returns a [Result] containing a list of successive accumulation values generated by applying
 * the fallible [operation] from left to right to each element and current accumulator value that
 * starts with [initial] value, returning early with the first [Err] if an [operation] fails.
 *
 * This function is an alias of [tryRunningFold].
 */
public inline fun <T, R, E> Iterable<T>.tryScan(
    initial: R,
    operation: (acc: R, T) -> Result<R, E>,
): Result<List<R>, E> {
    return tryRunningFold(initial, operation)
}

/**
 * Returns a [Result] containing a list of successive accumulation values generated by applying
 * the fallible [operation] from left to right to each element, its index in the original
 * collection, and current accumulator value that starts with [initial] value, returning early
 * with the first [Err] if an [operation] fails.
 *
 * This function is an alias of [tryRunningFoldIndexed].
 */
public inline fun <T, R, E> Iterable<T>.tryScanIndexed(
    initial: R,
    operation: (index: Int, acc: R, T) -> Result<R, E>,
): Result<List<R>, E> {
    return tryRunningFoldIndexed(initial, operation)
}

/**
 * Returns a [Result] containing a [Pair] of lists, where the [first][Pair.first] list contains
 * elements for which the fallible [predicate] returns [Ok]`(true)`, and the [second][Pair.second]
 * list contains elements for which the fallible [predicate] returns [Ok]`(false)`, returning
 * early with the first [Err] if the [predicate] fails.
 */
public inline fun <T, E> Iterable<T>.tryPartition(
    predicate: (T) -> Result<Boolean, E>,
): Result<Pair<List<T>, List<T>>, E> {
    return tryPartitionTo(ArrayList(), ArrayList(), predicate)
}

/**
 * Appends elements for which the fallible [predicate] returns [Ok]`(true)` to the [first]
 * destination, and elements for which it returns [Ok]`(false)` to the [second] destination,
 * returning early with the first [Err] if the [predicate] fails.
 */
public inline fun <T, E, C1 : MutableCollection<in T>, C2 : MutableCollection<in T>> Iterable<T>.tryPartitionTo(
    first: C1,
    second: C2,
    predicate: (T) -> Result<Boolean, E>,
): Result<Pair<C1, C2>, E> {
    for (element in this) {
        val result = predicate(element)

        when {
            result.isErr -> return Err(result.error)
            result.value -> first.add(element)
            else -> second.add(element)
        }
    }

    return Ok(Pair(first, second))
}
