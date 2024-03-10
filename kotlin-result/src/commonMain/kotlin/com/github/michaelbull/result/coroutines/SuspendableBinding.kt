package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.BindException
import com.github.michaelbull.result.BindingScope
import com.github.michaelbull.result.BindingScopeImpl
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Suspending variant of [binding][com.github.michaelbull.result.binding].
 */
@Deprecated(
    message = "Will throw a runtime exception if used with async requests that fail to bind. " +
        "See https://github.com/michaelbull/kotlin-result/pull/28 " +
        "Please import the kotlin-result-coroutines library to continue using this feature.",
    level = DeprecationLevel.WARNING
)
public suspend inline fun <V, E> binding(crossinline block: suspend BindingScope<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return with(BindingScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (ex: BindException) {
            result!!
        }
    }
}
