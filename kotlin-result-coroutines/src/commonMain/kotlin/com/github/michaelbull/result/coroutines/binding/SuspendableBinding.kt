package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

/**
 * Suspending variant of [binding][com.github.michaelbull.result.binding].
 * The suspendable [block] runs in a new [CoroutineScope], inheriting the parent [CoroutineContext].
 * This new scope is [cancelled][CoroutineScope.cancel] once a failing bind is encountered, eagerly cancelling all
 * child [jobs][Job].
 */
public suspend inline fun <V, E> binding(crossinline block: suspend SuspendableResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    lateinit var receiver: SuspendableResultBindingImpl<E>

    return try {
        coroutineScope {
            receiver = SuspendableResultBindingImpl(this.coroutineContext)
            with(receiver) { Ok(block()) }
        }
    } catch (ex: BindCancellationException) {
        receiver.result
    }
}

internal object BindCancellationException : CancellationException(null as String?)

public interface SuspendableResultBinding<E> : CoroutineScope {
    public suspend fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class SuspendableResultBindingImpl<E>(
    override val coroutineContext: CoroutineContext,
) : SuspendableResultBinding<E> {

    private val mutex = Mutex()
    lateinit var result: Err<E>

    override suspend fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> mutex.withLock {
                if (::result.isInitialized.not()) {
                    result = this
                    this@SuspendableResultBindingImpl.cancel(BindCancellationException)
                }

                throw BindCancellationException
            }
        }
    }
}
