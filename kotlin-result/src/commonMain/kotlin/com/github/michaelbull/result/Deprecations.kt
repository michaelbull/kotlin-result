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
