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
suspend inline fun <V, E> binding(crossinline block: suspend ResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val receiver = ResultBindingImpl<E>()

    return try {
        with(receiver) { Ok(block()) }
    } catch (ex: BindException) {
        receiver.error
    }
}
