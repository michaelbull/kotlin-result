package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] with [BindingScope] as its receiver and returns its [Result].
 *
 * When inside a [binding] block, the [bind][BindingScope.bind] function is accessible on any [Result]. Calling the
 * [bind][BindingScope.bind] function will attempt to unwrap the [Result] and locally return its [value][Ok.value]. If
 * the [Result] is an [Err], the binding block will terminate with that bind and return that failed-to-bind [Err].
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
 */
public inline fun <V, E> binding(crossinline block: BindingScope<E>.() -> V): Result<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return with(BindingScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (ex: BindException) {
            result!!
        }
    }
}

internal expect object BindException : Exception

@Deprecated(
    message = "Use BindingScope instead",
    replaceWith = ReplaceWith("BindingScope<E>")
)
public typealias ResultBinding<E> = BindingScope<E>

public interface BindingScope<E> {
    public fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class BindingScopeImpl<E> : BindingScope<E> {

    var result: Result<Nothing, E>? = null

    override fun <V> Result<V, E>.bind(): V {
        return when (this) {
            is Ok -> value
            is Err -> {
                this@BindingScopeImpl.result = this
                throw BindException
            }
        }
    }
}
