package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.asErr
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform

/**
 * Returns a [Result] containing a list of elements for which the fallible [predicate] returns
 * [Ok]`(true)`, returning early with the first [Err] if the [predicate] fails.
 *
 * This is the fallible equivalent of [Flow.filter][kotlinx.coroutines.flow.filter] and the [Flow]
 * equivalent of [Iterable.tryFilter][com.github.michaelbull.result.tryFilter].
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public suspend inline fun <T, E> Flow<T>.tryFilter(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<List<T>, E> {
    val values = ArrayList<T>()

    val firstError = transform { element ->
        val result = predicate(element)

        when {
            result.isErr -> emit(result)
            result.value -> values.add(element)
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(values)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a list of elements for which the fallible [predicate] returns
 * [Ok]`(false)`, returning early with the first [Err] if the [predicate] fails.
 *
 * This is the fallible equivalent of [Flow.filterNot][kotlinx.coroutines.flow.filterNot] and the
 * [Flow] equivalent of [Iterable.tryFilterNot][com.github.michaelbull.result.tryFilterNot].
 *
 * - Haskell: [Control.Monad.filterM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:filterM)
 */
public suspend inline fun <T, E> Flow<T>.tryFilterNot(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<List<T>, E> {
    val values = ArrayList<T>()

    val firstError = transform { element ->
        val result = predicate(element)

        when {
            result.isErr -> emit(result)
            !result.value -> values.add(element)
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(values)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each value of the original flow, returning early with the first [Err] if a
 * transformation fails.
 *
 * This is the fallible equivalent of [Flow.map][kotlinx.coroutines.flow.map] and the [Flow]
 * equivalent of [Iterable.tryMap][com.github.michaelbull.result.tryMap].
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public suspend inline fun <V, E, U> Flow<V>.tryMap(
    crossinline transform: suspend (V) -> Result<U, E>,
): Result<List<U>, E> {
    val values = mutableListOf<U>()

    val firstError = transform { element ->
        val result = transform(element)

        if (result.isOk) {
            values.add(result.value)
        } else {
            emit(result)
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(values)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result<List<U>, E>][Result] containing only the non-null results of applying the
 * given [transform] function to each value of the original flow, returning early with the first
 * [Err] if a transformation fails.
 *
 * This is the fallible equivalent of [Flow.mapNotNull][kotlinx.coroutines.flow.mapNotNull] and
 * the [Flow] equivalent of
 * [Iterable.tryMapNotNull][com.github.michaelbull.result.tryMapNotNull].
 *
 * - Gleam: [list.try_map](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_map)
 * - Haskell: [Data.Traversable.traverse](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:traverse)
 */
public suspend inline fun <V, E, U : Any> Flow<V>.tryMapNotNull(
    crossinline transform: suspend (V) -> Result<U, E>?,
): Result<List<U>, E> {
    val values = mutableListOf<U>()

    val firstError = transform { element ->
        val result = transform(element)

        if (result != null) {
            if (result.isOk) {
                values.add(result.value)
            } else {
                emit(result)
            }
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(values)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a single list of all elements yielded from the fallible
 * [transform] function being invoked on each value of the original flow, returning early with
 * the first [Err] if the [transform] fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryFlatMap][com.github.michaelbull.result.tryFlatMap].
 */
public suspend inline fun <T, U, E> Flow<T>.tryFlatMap(
    crossinline transform: suspend (T) -> Result<Iterable<U>, E>,
): Result<List<U>, E> {
    val values = ArrayList<U>()

    val firstError = transform { element ->
        val result = transform(element)

        if (result.isOk) {
            values.addAll(result.value)
        } else {
            emit(Err(result.error))
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(values)
        else -> firstError.asErr()
    }
}

/**
 * Performs the given fallible [action] on each value of the flow, returning early with the first
 * [Err] if an [action] fails.
 *
 * This is the fallible equivalent of [Flow.collect][kotlinx.coroutines.flow.collect] and the
 * [Flow] equivalent of [Iterable.tryForEach][com.github.michaelbull.result.tryForEach].
 *
 * - Gleam: [list.try_each](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_each)
 * - Rust: [Iterator::try_for_each](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_for_each)
 */
public suspend inline fun <V, E> Flow<V>.tryForEach(
    crossinline action: suspend (V) -> Result<*, E>,
): Result<Unit, E> {
    val firstError = transform { element ->
        val result = action(element)

        if (result.isErr) {
            emit(Err(result.error))
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(Unit)
        else -> firstError.asErr()
    }
}

/**
 * Accumulates value starting with the first element and applying [operation] from left to right to
 * current accumulator value and each element, returning early with the first [Err] if an
 * [operation] fails. Returns `null` if the flow is empty.
 *
 * This is the fallible equivalent of [Flow.reduce][kotlinx.coroutines.flow.reduce] and the [Flow]
 * equivalent of [Iterable.tryReduce][com.github.michaelbull.result.tryReduce].
 *
 * - Rust: [Iterator::try_reduce](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_reduce)
 */
public suspend inline fun <S, T : S, E> Flow<T>.tryReduce(
    crossinline operation: suspend (acc: S, T) -> Result<S, E>,
): Result<S, E>? {
    var accumulator: S? = null
    var hasValue = false

    val firstError = transform { element ->
        if (!hasValue) {
            accumulator = element
            hasValue = true
        } else {
            @Suppress("UNCHECKED_CAST")
            val result = operation(accumulator as S, element)

            if (result.isOk) {
                accumulator = result.value
            } else {
                emit(result)
            }
        }
    }.firstOrNull()

    return when {
        firstError != null -> firstError.asErr()
        hasValue -> {
            @Suppress("UNCHECKED_CAST")
            Ok(accumulator as S)
        }

        else -> null
    }
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right to
 * current accumulator value and each element.
 *
 * This is the fallible equivalent of [Flow.fold][kotlinx.coroutines.flow.fold] and the [Flow]
 * equivalent of [Iterable.tryFold][com.github.michaelbull.result.tryFold].
 *
 * - Gleam: [list.try_fold](https://hexdocs.pm/gleam_stdlib/gleam/list.html#try_fold)
 * - Haskell: [Control.Monad.foldM](https://hackage.haskell.org/package/base/docs/Control-Monad.html#v:foldM)
 * - Rust: [Iterator::try_fold](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_fold)
 */
public suspend inline fun <T, R, E> Flow<T>.tryFold(
    initial: R,
    crossinline operation: suspend (acc: R, T) -> Result<R, E>,
): Result<R, E> {
    var accumulator = initial

    val firstError = transform { element ->
        val result = operation(accumulator, element)

        if (result.isOk) {
            accumulator = result.value
        } else {
            emit(result)
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(accumulator)
        else -> firstError.asErr()
    }
}

/**
 * Returns the first element for which the fallible [predicate] returns [Ok]`(true)`, returning
 * early with the first [Err] if the [predicate] fails. Returns `null` if no matching element is
 * found.
 *
 * This is the fallible equivalent of [Flow.firstOrNull][kotlinx.coroutines.flow.firstOrNull] and
 * the [Flow] equivalent of [Iterable.tryFind][com.github.michaelbull.result.tryFind].
 *
 * - Rust: [Iterator::try_find](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_find)
 */
public suspend inline fun <T, E> Flow<T>.tryFind(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<T, E>? {
    return transform { element ->
        val result = predicate(element)

        when {
            result.isErr -> emit(Err(result.error))
            result.value -> emit(Ok(element))
        }
    }.firstOrNull()
}

/**
 * Returns the last element for which the fallible [predicate] returns [Ok]`(true)`, returning
 * early with the first [Err] if the [predicate] fails. Returns `null` if no matching element is
 * found.
 *
 * This is the fallible equivalent of [Flow.lastOrNull][kotlinx.coroutines.flow.lastOrNull] and
 * the [Flow] equivalent of [Iterable.tryFindLast][com.github.michaelbull.result.tryFindLast].
 *
 * - Rust: [Iterator::try_find](https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.try_find)
 */
public suspend inline fun <T, E> Flow<T>.tryFindLast(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<T, E>? {
    var last: T? = null
    var found = false

    val firstError = transform { element ->
        val result = predicate(element)

        when {
            result.isErr -> emit(Err(result.error))
            result.value -> {
                last = element
                found = true
            }
        }
    }.firstOrNull()

    return when {
        firstError != null -> firstError.asErr()
        found -> {
            @Suppress("UNCHECKED_CAST")
            Ok(last as T)
        }

        else -> null
    }
}

/**
 * Returns a [Result] containing a [Map] of key-value pairs provided by the fallible [transform]
 * function applied to each value of the original flow, returning early with the first [Err] if
 * the [transform] fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryAssociate][com.github.michaelbull.result.tryAssociate].
 */
public suspend inline fun <T, K, V, E> Flow<T>.tryAssociate(
    crossinline transform: suspend (T) -> Result<Pair<K, V>, E>,
): Result<Map<K, V>, E> {
    val destination = LinkedHashMap<K, V>()

    val firstError = transform { element ->
        val result = transform(element)

        if (result.isOk) {
            val (key, value) = result.value
            destination[key] = value
        } else {
            emit(Err(result.error))
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(destination)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function applied to each value of the original flow, and values are the elements themselves,
 * returning early with the first [Err] if the [keySelector] fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryAssociateBy][com.github.michaelbull.result.tryAssociateBy].
 */
public suspend inline fun <T, K, E> Flow<T>.tryAssociateBy(
    crossinline keySelector: suspend (T) -> Result<K, E>,
): Result<Map<K, T>, E> {
    val destination = LinkedHashMap<K, T>()

    val firstError = transform { element ->
        val keyResult = keySelector(element)

        if (keyResult.isOk) {
            destination[keyResult.value] = element
        } else {
            emit(Err(keyResult.error))
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(destination)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function and values are provided by the fallible [valueTransform] function, both applied to
 * each value of the original flow, returning early with the first [Err] if either function fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryAssociateBy][com.github.michaelbull.result.tryAssociateBy].
 */
public suspend inline fun <T, K, V, E> Flow<T>.tryAssociateBy(
    crossinline keySelector: suspend (T) -> Result<K, E>,
    crossinline valueTransform: suspend (T) -> Result<V, E>,
): Result<Map<K, V>, E> {
    val destination = LinkedHashMap<K, V>()

    val firstError = transform { element ->
        val keyResult = keySelector(element)

        if (keyResult.isErr) {
            emit(Err(keyResult.error))
        } else {
            val valueResult = valueTransform(element)

            if (valueResult.isErr) {
                emit(Err(valueResult.error))
            } else {
                destination[keyResult.value] = valueResult.value
            }
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(destination)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a [Map] where keys are elements of the original flow and values
 * are provided by the fallible [valueSelector] function applied to each element, returning early
 * with the first [Err] if the [valueSelector] fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryAssociateWith][com.github.michaelbull.result.tryAssociateWith].
 */
public suspend inline fun <K, V, E> Flow<K>.tryAssociateWith(
    crossinline valueSelector: suspend (K) -> Result<V, E>,
): Result<Map<K, V>, E> {
    val destination = LinkedHashMap<K, V>()

    val firstError = transform { element ->
        val result = valueSelector(element)

        if (result.isOk) {
            destination[element] = result.value
        } else {
            emit(Err(result.error))
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(destination)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function applied to each value of the original flow, and values are lists of elements
 * corresponding to each key, returning early with the first [Err] if the [keySelector] fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryGroupBy][com.github.michaelbull.result.tryGroupBy].
 */
public suspend inline fun <T, K, E> Flow<T>.tryGroupBy(
    crossinline keySelector: suspend (T) -> Result<K, E>,
): Result<Map<K, List<T>>, E> {
    val destination = LinkedHashMap<K, MutableList<T>>()

    val firstError = transform { element ->
        val keyResult = keySelector(element)

        if (keyResult.isOk) {
            val list = destination.getOrPut(keyResult.value) { ArrayList() }
            list.add(element)
        } else {
            emit(Err(keyResult.error))
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(destination)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a [Map] where keys are provided by the fallible [keySelector]
 * function and values are lists of results of the fallible [valueTransform] function, both
 * applied to each value of the original flow, returning early with the first [Err] if either
 * function fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryGroupBy][com.github.michaelbull.result.tryGroupBy].
 */
public suspend inline fun <T, K, V, E> Flow<T>.tryGroupBy(
    crossinline keySelector: suspend (T) -> Result<K, E>,
    crossinline valueTransform: suspend (T) -> Result<V, E>,
): Result<Map<K, List<V>>, E> {
    val destination = LinkedHashMap<K, MutableList<V>>()

    val firstError = transform { element ->
        val keyResult = keySelector(element)

        if (keyResult.isErr) {
            emit(Err(keyResult.error))
        } else {
            val valueResult = valueTransform(element)

            if (valueResult.isErr) {
                emit(Err(valueResult.error))
            } else {
                val list = destination.getOrPut(keyResult.value) { ArrayList() }
                list.add(valueResult.value)
            }
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(destination)
        else -> firstError.asErr()
    }
}

/**
 * Returns a [Result] containing a [Pair] of lists, where the [first][Pair.first] list contains
 * elements for which the fallible [predicate] returns [Ok]`(true)`, and the [second][Pair.second]
 * list contains elements for which the fallible [predicate] returns [Ok]`(false)`, returning
 * early with the first [Err] if the [predicate] fails.
 *
 * This is the [Flow] equivalent of
 * [Iterable.tryPartition][com.github.michaelbull.result.tryPartition].
 */
public suspend inline fun <T, E> Flow<T>.tryPartition(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<Pair<List<T>, List<T>>, E> {
    val first = ArrayList<T>()
    val second = ArrayList<T>()

    val firstError = transform { element ->
        val result = predicate(element)

        when {
            result.isErr -> emit(result)
            result.value -> first.add(element)
            else -> second.add(element)
        }
    }.firstOrNull()

    return when (firstError) {
        null -> Ok(Pair(first, second))
        else -> firstError.asErr()
    }
}
