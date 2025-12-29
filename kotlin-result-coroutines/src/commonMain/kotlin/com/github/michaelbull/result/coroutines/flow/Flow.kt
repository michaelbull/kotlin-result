package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.asOk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.all
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

/**
 * Returns a flow containing only values of the original flow that [are ok][Result.isOk].
 *
 * This is the [Flow] equivalent of [Iterable.filterOk][com.github.michaelbull.result.filterOk].
 *
 * - Gleam: [result.values](https://hexdocs.pm/gleam_stdlib/gleam/result.html#values)
 */
public fun <V, E> Flow<Result<V, E>>.filterOk(): Flow<V> {
    return transform { result ->
        if (result.isOk) {
            emit(result.value)
        }
    }
}

/**
 * Returns a flow containing only values of the original flow that [are an error][Result.isErr].
 *
 * This is the [Flow] equivalent of [Iterable.filterErr][com.github.michaelbull.result.filterErr].
 */
public fun <V, E> Flow<Result<V, E>>.filterErr(): Flow<E> {
    return transform { result ->
        if (result.isErr) {
            emit(result.error)
        }
    }
}

/**
 * Returns a flow that invokes the given [action] on each [ok][Result.isOk] value of the upstream
 * flow before it is emitted downstream.
 *
 * This is the [Flow] equivalent of [Iterable.onEachOk][com.github.michaelbull.result.onEachOk].
 */
@IgnorableReturnValue
public inline fun <V, E> Flow<Result<V, E>>.onEachOk(
    crossinline action: suspend (V) -> Unit,
): Flow<Result<V, E>> {
    return onEach { result ->
        if (result.isOk) {
            action(result.value)
        }
    }
}

/**
 * Returns a flow that invokes the given [action] on each [error][Result.isErr] value of the
 * upstream flow before it is emitted downstream.
 *
 * This is the [Flow] equivalent of [Iterable.onEachErr][com.github.michaelbull.result.onEachErr].
 */
@IgnorableReturnValue
public inline fun <V, E> Flow<Result<V, E>>.onEachErr(
    crossinline action: suspend (E) -> Unit,
): Flow<Result<V, E>> {
    return onEach { result ->
        if (result.isErr) {
            action(result.error)
        }
    }
}

/**
 * Returns `true` if each element [is ok][Result.isOk], `false` otherwise.
 *
 * This is the [Flow] equivalent of [Iterable.allOk][com.github.michaelbull.result.allOk].
 */
public suspend fun <V, E> Flow<Result<V, E>>.allOk(): Boolean {
    return all { it.isOk }
}

/**
 * Returns `true` if each element [is an error][Result.isErr], `false` otherwise.
 *
 * This is the [Flow] equivalent of [Iterable.allErr][com.github.michaelbull.result.allErr].
 */
public suspend fun <V, E> Flow<Result<V, E>>.allErr(): Boolean {
    return all { it.isErr }
}

/**
 * Returns `true` if at least one element [is ok][Result.isOk], `false` otherwise.
 *
 * This is the [Flow] equivalent of [Iterable.anyOk][com.github.michaelbull.result.anyOk].
 */
public suspend fun <V, E> Flow<Result<V, E>>.anyOk(): Boolean {
    return any { it.isOk }
}

/**
 * Returns `true` if at least one element [is an error][Result.isErr], `false` otherwise.
 *
 * This is the [Flow] equivalent of [Iterable.anyErr][com.github.michaelbull.result.anyErr].
 */
public suspend fun <V, E> Flow<Result<V, E>>.anyErr(): Boolean {
    return any { it.isErr }
}

/**
 * Returns the number of elements that [are ok][Result.isOk].
 *
 * This is the [Flow] equivalent of [Iterable.countOk][com.github.michaelbull.result.countOk].
 */
public suspend fun <V, E> Flow<Result<V, E>>.countOk(): Int {
    return count { it.isOk }
}

/**
 * Returns the number of elements that [are an error][Result.isErr].
 *
 * This is the [Flow] equivalent of [Iterable.countErr][com.github.michaelbull.result.countErr].
 */
public suspend fun <V, E> Flow<Result<V, E>>.countErr(): Int {
    return count { it.isErr }
}

/**
 * Partitions this flow into a [Pair] of [Lists][List]. An element that [is ok][Result.isOk] will
 * appear in the [first][Pair.first] list, whereas an element that [is an error][Result.isErr] will
 * appear in the [second][Pair.second] list.
 *
 * This is the [Flow] equivalent of [Iterable.partition][com.github.michaelbull.result.partition].
 *
 * - Gleam: [result.partition](https://hexdocs.pm/gleam_stdlib/gleam/result.html#partition)
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base/docs/Data-Either.html#v:partitionEithers)
 */
public suspend fun <V, E> Flow<Result<V, E>>.partition(): Pair<List<V>, List<E>> {
    val values = mutableListOf<V>()
    val errors = mutableListOf<E>()

    collect { result ->
        if (result.isOk) {
            values += result.value
        } else {
            errors += result.error
        }
    }

    return Pair(values, errors)
}

/**
 * Combines [this] flow into a single [Result] (holding a [List]). Elements in the returned list
 * are in the same order as [this].
 *
 * - If all results [are ok][Result.isOk], returns [Ok] with all values.
 * - If any result [is an error][Result.isErr], returns the first [Err] encountered.
 * - If the flow is empty, returns [Ok] with an empty list.
 *
 * This is the [Flow] equivalent of [Iterable.combine][com.github.michaelbull.result.combine].
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#combine)
 * - Gleam: [result.all](https://hexdocs.pm/gleam_stdlib/gleam/result.html#all)
 * - Haskell: [Data.Traversable.sequenceA](https://hackage.haskell.org/package/base/docs/Data-Traversable.html#v:sequenceA)
 */
public suspend fun <V, E> Flow<Result<V, E>>.combine(): Result<List<V>, E> {
    val values = mutableListOf<V>()

    val firstError = transform { result ->
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
 * Combines [this] flow into a single [Result] (holding a [List] of errors). Elements in the
 * returned list are in the same order as [this].
 *
 * - If all results [are errors][Result.isErr], returns [Err] with all errors.
 * - If any result [is ok][Result.isOk], returns the first [Ok] encountered.
 * - If the flow is empty, returns [Err] with an empty list.
 *
 * This is the [Flow] equivalent of [Iterable.combineErr][com.github.michaelbull.result.combineErr].
 */
public suspend fun <V, E> Flow<Result<V, E>>.combineErr(): Result<V, List<E>> {
    val errors = mutableListOf<E>()

    val firstOk = transform { result ->
        if (result.isErr) {
            errors.add(result.error)
        } else {
            emit(result)
        }
    }.firstOrNull()

    return when (firstOk) {
        null -> Err(errors)
        else -> firstOk.asOk()
    }
}
