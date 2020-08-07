package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Suspending variant of [binding][com.github.michaelbull.result.binding].
 */
suspend inline fun <V, E> binding(crossinline block: suspend SuspendableResultBinding<E>.() -> V): Result<V, E> {
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

interface SuspendableResultBinding<E> {
    suspend fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class SuspendableResultBindingImpl<E> : SuspendableResultBinding<E> {

    private val mutex = Mutex()
    lateinit var internalError: Err<E>

    override suspend fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> {
                mutex.withLock {
                    if (::internalError.isInitialized.not()){
                        internalError = this
                    }
                }
                throw BindCancellationException
            }
        }
    }
}
