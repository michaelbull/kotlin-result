package com.github.michaelbull.result

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
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails. Elements in the returned list are in the same order as [this].
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Traversable.html#v:traverse)
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
 * - Haskell: [Control.Monad.foldM](https://hackage.haskell.org/package/base-4.10.0.0/docs/Control-Monad.html#v:foldM)
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
 * - Haskell: [Control.Monad.foldM](https://hackage.haskell.org/package/base-4.10.0.0/docs/Control-Monad.html#v:foldM)
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
