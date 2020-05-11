package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] with [ResultBinding] as its receiver and returns its [Result].
 *
 * When inside a [binding] block, the [bind][ResultBinding.bind] function is accessible on any [Result]. Calling the
 * [bind][ResultBinding.bind] function will attempt to unwrap the [Result] and locally return its [value][Ok.value]. If
 * the [Result] is an [Err], the binding block will terminate early and return the first [error][Err.error].
 *
 * Example:
 * ```
 * fun provideX(): Result<Int, ExampleErr> { ... }
 * fun provideY(): Result<Int, ExampleErr> { ... }
 *
 * val result: Result<Int, ExampleErr> = binding {
 *   val x = provideX().bind()
 *   val y = provideY().bind()
 *   x + y
 * }
 * ```
 *
 * @sample com.github.michaelbull.result.bind.ResultBindingTest
 */
inline fun <V, E> binding(crossinline block: ResultBinding<E>.() -> V): Result<V, E> {
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

internal object BindException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

interface ResultBinding<E> {
    fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class ResultBindingImpl<E> : ResultBinding<E> {

    lateinit var error: Err<E>

    override fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> {
                this@ResultBindingImpl.error = this
                throw BindException
            }
        }
    }
}
