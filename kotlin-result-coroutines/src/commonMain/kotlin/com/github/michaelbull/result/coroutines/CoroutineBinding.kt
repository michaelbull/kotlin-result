package com.github.michaelbull.result.coroutines

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
public suspend inline fun <V, E> coroutineBinding(crossinline block: suspend CoroutineBindingScope<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    lateinit var receiver: CoroutineBindingScopeImpl<E>

    return try {
        coroutineScope {
            receiver = CoroutineBindingScopeImpl(this)

            with(receiver) {
                Ok(block())
            }
        }
    } catch (ex: BindCancellationException) {
        receiver.result!!
    }
}

internal object BindCancellationException : CancellationException(null as String?)

public interface CoroutineBindingScope<E> : CoroutineScope {
    public suspend fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class CoroutineBindingScopeImpl<E>(
    delegate: CoroutineScope,
) : CoroutineBindingScope<E>, CoroutineScope by delegate {

    private val mutex = Mutex()
    var result: Result<Nothing, E>? = null

    override suspend fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> mutex.withLock {
                if (result == null) {
                    result = this
                    coroutineContext.cancel(BindCancellationException)
                }

                throw BindCancellationException
            }
        }
    }
}
