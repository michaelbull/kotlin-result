package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Deprecated("Use filterOk instead.", ReplaceWith("filterOk()"))
public fun <V, E> Iterable<Result<V, E>>.filterValues(): List<V> {
    return filterOk()
}

@Deprecated("Use filterErr instead.", ReplaceWith("filterErr()"))
public fun <V, E> Iterable<Result<V, E>>.filterErrors(): List<E> {
    return filterErr()
}

@Deprecated("Use filterOkTo instead.", ReplaceWith("filterOkTo(destination)"))
public fun <V, E, C : MutableCollection<in V>> Iterable<Result<V, E>>.filterValuesTo(destination: C): C {
    return filterOkTo(destination)
}

@Deprecated("Use filterErrTo instead.", ReplaceWith("filterErrTo(destination)"))
public fun <V, E, C : MutableCollection<in E>> Iterable<Result<V, E>>.filterErrorsTo(destination: C): C {
    return filterErrTo(destination)
}

@Deprecated("Use onOk instead.", ReplaceWith("onOk(action)"))
public inline infix fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    return onOk(action)
}

@Deprecated("Use onErr instead.", ReplaceWith("onErr(action)"))
public inline infix fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    return onErr(action)
}

@Deprecated("Use tryMap instead.", ReplaceWith("tryMap(transform)"))
public inline fun <V, E, U> Iterable<V>.mapResult(
    transform: (V) -> Result<U, E>,
): Result<List<U>, E> {
    return tryMap(transform)
}

@Deprecated("Use tryMapTo instead.", ReplaceWith("tryMapTo(destination, transform)"))
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultTo(
    destination: C,
    transform: (V) -> Result<U, E>,
): Result<C, E> {
    return tryMapTo(destination, transform)
}

@Deprecated("Use tryMapNotNull instead.", ReplaceWith("tryMapNotNull(transform)"))
public inline fun <V, E, U : Any> Iterable<V>.mapResultNotNull(
    transform: (V) -> Result<U, E>?,
): Result<List<U>, E> {
    return tryMapNotNull(transform)
}

@Deprecated("Use tryMapNotNullTo instead.", ReplaceWith("tryMapNotNullTo(destination, transform)"))
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.mapResultNotNullTo(
    destination: C,
    transform: (V) -> Result<U, E>?,
): Result<C, E> {
    return tryMapNotNullTo(destination, transform)
}

@Deprecated("Use tryMapIndexed instead.", ReplaceWith("tryMapIndexed(transform)"))
public inline fun <V, E, U> Iterable<V>.mapResultIndexed(
    transform: (index: Int, V) -> Result<U, E>,
): Result<List<U>, E> {
    return tryMapIndexed(transform)
}

@Deprecated("Use tryMapIndexedTo instead.", ReplaceWith("tryMapIndexedTo(destination, transform)"))
public inline fun <V, E, U, C : MutableCollection<in U>> Iterable<V>.mapResultIndexedTo(
    destination: C,
    transform: (index: Int, V) -> Result<U, E>,
): Result<C, E> {
    return tryMapIndexedTo(destination, transform)
}

@Deprecated("Use tryMapIndexedNotNull instead.", ReplaceWith("tryMapIndexedNotNull(transform)"))
public inline fun <V, E, U : Any> Iterable<V>.mapResultIndexedNotNull(
    transform: (index: Int, V) -> Result<U, E>?,
): Result<List<U>, E> {
    return tryMapIndexedNotNull(transform)
}

@Deprecated("Use tryMapIndexedNotNullTo instead.", ReplaceWith("tryMapIndexedNotNullTo(destination, transform)"))
public inline fun <V, E, U : Any, C : MutableCollection<in U>> Iterable<V>.mapResultIndexedNotNullTo(
    destination: C,
    transform: (index: Int, V) -> Result<U, E>?,
): Result<C, E> {
    return tryMapIndexedNotNullTo(destination, transform)
}

@Deprecated("Use tryFold instead.", ReplaceWith("tryFold(initial, operation)"))
public inline fun <T, R, E> Iterable<T>.fold(
    initial: R,
    operation: (acc: R, T) -> Result<R, E>,
): Result<R, E> {
    return tryFold(initial, operation)
}

@Deprecated("Use tryFoldRight instead.", ReplaceWith("tryFoldRight(initial, operation)"))
public inline fun <T, R, E> List<T>.foldRight(
    initial: R,
    operation: (T, acc: R) -> Result<R, E>,
): Result<R, E> {
    return tryFoldRight(initial, operation)
}

@Deprecated("Use tryMap instead.", ReplaceWith("tryMap(transform)"))
public inline infix fun <V, E, U> Result<Iterable<V>, E>.mapAll(transform: (V) -> Result<U, E>): Result<List<U>, E> {
    return tryMap(transform)
}
