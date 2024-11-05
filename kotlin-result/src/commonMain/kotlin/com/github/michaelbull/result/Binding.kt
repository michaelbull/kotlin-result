package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] with [BindingScope] as its receiver and returns its
 * [Result].
 *
 * When inside a binding [block], the [bind][BindingScope.bind] function is accessible on any
 * [Result]. Calling the [bind][BindingScope.bind] function will attempt to unwrap the [Result]
 * and locally return its [value][Result.value].
 *
 * If a [bind][BindingScope.bind] returns an error, the [block] will terminate immediately.
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
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return with(BindingScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (_: BindException) {
            result!!
        }
    }
}

internal expect object BindException : Exception

public interface BindingScope<E> {
    public fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class BindingScopeImpl<E> : BindingScope<E> {

    var result: Result<Nothing, E>? = null

    override fun <V> Result<V, E>.bind(): V {
        return if (isOk) {
            value
        } else {
            result = this.asErr()
            throw BindException
        }
    }
}
