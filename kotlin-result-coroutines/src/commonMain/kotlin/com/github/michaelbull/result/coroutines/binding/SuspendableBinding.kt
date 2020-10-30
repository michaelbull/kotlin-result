package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Suspending variant of [binding][com.github.michaelbull.result.binding].
 */
public suspend inline fun <V, E> binding(eagerlyCancel: Boolean = false, crossinline block: suspend SuspendableResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val receiver = SuspendableResultBindingImpl<E>(eagerlyCancel)

    return try {
        coroutineScope {
            receiver.coroutineScope = this@coroutineScope
            with(receiver) { Ok(block()) }
        }
    } catch (ex: BindCancellationException) {
        receiver.internalError
    }



}

internal object BindCancellationException : CancellationException(null)

public interface SuspendableResultBinding<E> {
    public suspend fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class SuspendableResultBindingImpl<E>(private val eagerlyCancel: Boolean) : SuspendableResultBinding<E> {

    private val mutex = Mutex()
    lateinit var internalError: Err<E>
    var coroutineScope: CoroutineScope? = null

    override suspend fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> {
                mutex.withLock {
                    if (::internalError.isInitialized.not()) {
                        internalError = this
                    }
                }
                if (eagerlyCancel) {
                    coroutineScope?.cancel(BindCancellationException)
                }
                throw BindCancellationException
            }
        }
    }
}
