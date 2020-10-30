package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Suspending variant of [binding][com.github.michaelbull.result.binding].
 */
public suspend inline fun <V, E> binding(crossinline block: suspend SuspendableResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val receiver = SuspendableResultBindingImpl<E>(eagerlyCancel = false)

    return try {
        with(receiver) { Ok(block()) }
    } catch (ex: BindCancellationException) {
        receiver.internalError
    }
}

/**
 * For use with [kotlinx.coroutines.async] wrapped binds. Eagerly cancels all deferred jobs once a failing bind is encountered.
 * A Suspending variant of [binding][com.github.michaelbull.result.binding] that wraps the suspendable block in a new coroutine scope.
 * When any bind fails in this scope, the coroutine scope will be cancelled which in turn cancels its children.
 * This can be useful in cases where long running or computation heavy async suspending calls are not needed to complete once the first binding fails.
 */
public suspend inline fun <V, E> eagerlyCancelBinding(crossinline block: suspend SuspendableResultBinding<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val receiver = SuspendableResultBindingImpl<E>(eagerlyCancel = true)

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
