package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.binding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] with [CoroutineBindingScope] as its receiver and returns
 * its [Result].
 *
 * When inside a binding [block], the [bind][CoroutineBindingScope.bind] function is accessible on
 * any [Result]. Calling the [bind][CoroutineBindingScope.bind] function will attempt to unwrap the
 * [Result] and locally return its [value][Result.value].
 *
 * Unlike [binding], this function is designed for _concurrent decomposition_ of work. When any
 * [bind][CoroutineBindingScope.bind] returns an error, the [CoroutineScope] will be
 * [cancelled][Job.cancel], cancelling all the other children.
 *
 * This function returns as soon as the given [block] and all its child coroutines are completed.
 *
 * Example:
 * ```
 * suspend fun provideX(): Result<Int, ExampleErr> { ... }
 * suspend fun provideY(): Result<Int, ExampleErr> { ... }
 *
 * val result: Result<Int, ExampleErr> = coroutineBinding {
 *   val x = async { provideX().bind() }
 *   val y = async { provideY().bind() }
 *   x.await() + y.await()
 * }
 */
public suspend inline fun <V, E> coroutineBinding(crossinline block: suspend CoroutineBindingScope<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
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
        receiver.result ?: throw ex
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

    @OptIn(UnsafeResultValueAccess::class)
    override suspend fun <V> Result<V, E>.bind(): V {
        return if (isOk) {
            value
        } else {
            mutex.withLock {
                if (result == null) {
                    result = this.asErr()
                    coroutineContext.cancel(BindCancellationException)
                }

                throw BindCancellationException
            }
        }
    }
}
