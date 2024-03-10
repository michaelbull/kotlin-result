package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.BindException
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.ResultBinding
import com.github.michaelbull.result.ResultBindingImpl
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
public suspend inline fun <V, E> binding(crossinline block: suspend ResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return with(ResultBindingImpl<E>()) {
        try {
            Ok(block())
        } catch (ex: BindException) {
            result!!
        }
    }
}
