package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.ResultBinding
import kotlinx.coroutines.CancellationException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Suspending variant of [binding][com.github.michaelbull.result.binding].
 */
suspend inline fun <V, E> binding(crossinline block: suspend ResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val receiver = SuspendableResultBindingImpl<E>()

    return try {
        with(receiver) { Ok(block()) }
    } catch (ex: BindCancellationException) {
        receiver.internalError
    }
}

internal object BindCancellationException : CancellationException(null)

@PublishedApi
internal class SuspendableResultBindingImpl<E> : ResultBinding<E> {

    lateinit var internalError: Err<E>

    override fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> {
                if (::internalError.isInitialized.not()){
                    internalError = this
                }
                throw BindCancellationException
            }
        }
    }
}
