package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.BindingDsl
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.binding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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
 *   val x = async { provideX() }
 *   val y = async { provideY() }
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
    } catch (ex: BindingCancellationException) {
        receiver.result ?: throw ex
    }
}

public class BindingCancellationException : CancellationException(null as String?)

@BindingDsl
public interface CoroutineBindingScope<E> : CoroutineScope {

    /**
     * Unwraps this [Result], returning the [value][Result.value] if [Ok], or short-circuiting
     * the enclosing [coroutineBinding] block with the [error][Result.error] if [Err].
     *
     * Unlike [BindingScope.bind][com.github.michaelbull.result.BindingScope.bind], this
     * function also [cancels][CoroutineScope.cancel] the enclosing [CoroutineScope], cancelling
     * all other child coroutines.
     *
     * This is functionally equivalent to Rust's
     * [`?` operator](https://doc.rust-lang.org/std/result/index.html#the-question-mark-operator-).
     *
     * ```
     * suspend fun provideX(): Result<Int, ExampleErr> { ... }
     * suspend fun provideY(): Result<Int, ExampleErr> { ... }
     *
     * val result: Result<Int, ExampleErr> = coroutineBinding {
     *   val x = provideX().bind()
     *   val y = provideY().bind()
     *   x + y
     * }
     * ```
     */
    public suspend fun <V> Result<V, E>.bind(): V

    /**
     * Creates a coroutine and returns its future result as an implementation of [Deferred],
     * automatically [binding][bind] the [Result] returned by [block].
     *
     * This function shadows [CoroutineScope.async][kotlinx.coroutines.async] when [block]
     * returns a [Result]. It ensures that [bind] is called inside the [async] coroutine,
     * providing two benefits:
     *
     * 1. **Early cancellation**: if the [Result] is an error, the [CoroutineScope] is cancelled
     *    immediately — without waiting for the caller to [await][Deferred.await] the result.
     *
     * 2. **Preventing sequential execution**: without this shadowing, it is easy to
     *    accidentally write `async { ... }.await().bind()`, chaining [await][Deferred.await]
     *    immediately after [async]. This defeats the purpose of [async] as each call will
     *    suspend until completion before the next starts:
     *
     *    ```
     *    coroutineBinding {
     *        // BAD: sequential execution, each await suspends before the next async starts
     *        val x = async { provideX() }.await().bind()
     *        val y = async { provideY() }.await().bind()
     *        x + y
     *    }
     *    ```
     *
     *    By returning [Deferred]`<V>` instead of [Deferred]`<Result<V, E>>`, there is no
     *    [Result] to [bind] at the call site, making the sequential mistake a compile error.
     *    The correct concurrent pattern becomes the only natural option:
     *
     *    ```
     *    coroutineBinding {
     *        val x = async { provideX() }
     *        val y = async { provideY() }
     *        x.await() + y.await()
     *    }
     *    ```
     *
     * When [block] does not return a [Result], the standard
     * [CoroutineScope.async][kotlinx.coroutines.async] extension function is used instead.
     *
     * Coroutine context is inherited from this [CoroutineScope]. Additional context elements can
     * be specified with the [context] argument. If the context does not have any dispatcher nor
     * any other [ContinuationInterceptor][kotlin.coroutines.ContinuationInterceptor], then
     * [Dispatchers.Default][kotlinx.coroutines.Dispatchers.Default] is used. The parent job is
     * inherited from this [CoroutineScope] as well, but it can also be overridden with a
     * corresponding [context] element.
     *
     * By default, the coroutine is immediately scheduled for execution. Other options can be
     * specified via the [start] parameter. See [CoroutineStart] for details. An optional [start]
     * parameter can be set to [CoroutineStart.LAZY] to start the coroutine _lazily_. In this
     * case, the resulting [Deferred] is created in a _new_ state. It can be explicitly started
     * with the [start][Job.start] function and will be started implicitly on the first
     * invocation of [join][Job.join], [await][Deferred.await], or
     * [awaitAll][kotlinx.coroutines.awaitAll].
     *
     * @param context additional to [CoroutineScope.coroutineContext] context of the coroutine.
     * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
     * @param block the coroutine code which must return a [Result].
     */
    public fun <V> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Result<V, E>,
    ): Deferred<V>
}

@PublishedApi
internal class CoroutineBindingScopeImpl<E>(
    private val delegate: CoroutineScope,
) : CoroutineBindingScope<E>, CoroutineScope by delegate {

    private val mutex = Mutex()
    var result: Result<Nothing, E>? = null

    override fun <V> async(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Result<V, E>,
    ): Deferred<V> {
        return delegate.async(context, start) {
            block.invoke(this@CoroutineBindingScopeImpl).bind()
        }
    }

    override suspend fun <V> Result<V, E>.bind(): V {
        return if (isOk) {
            value
        } else {
            mutex.withLock {
                if (result == null) {
                    result = this.asErr()
                    coroutineContext.cancel(BindingCancellationException())
                }

                throw BindingCancellationException()
            }
        }
    }
}
