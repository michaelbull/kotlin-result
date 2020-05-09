package com.github.michaelbull.result.bind

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse

/**
 * Calls the specified function [block] with [ResultBindingImpl] as its receiver and returns a [Result] of either [Ok] with value [V] or [Err] with error [E].
 *
 * [ResultBinding.bind] extension function is made available to any [Result] object inside this [block].
 * [ResultBinding.bind] will attempt to unwrap the [Result] to return its [Ok] value locally.
 * If the result is a [Err], then the [binding] block terminates with that [ResultBinding.bind] and returns a [Result] containing the error of the failed binding.
 *
 * This allows for easy unwrapping of result type objects along a happy path.
 * for examples, see tests
 * @sample com.github.michaelbull.result.bind.ResultBindingTest
 *
 */
inline fun <V, E> binding(crossinline block: ResultBinding<E>.() -> V): Result<V, E> {
    return ResultBindingImpl.with(block)
}

@PublishedApi
internal class ResultBindingImpl<E> : ResultBinding<E> {

    lateinit var error: Err<E>

    override fun <V> Result<V, E>.bind(): V {
        return this.getOrElse {
            error = Err(it)
            throw BindFailure
        }
    }

    @PublishedApi
    internal companion object {
        inline fun <V, E> with(crossinline block: ResultBindingImpl<E>.() -> V): Result<V, E> {
            val context = ResultBindingImpl<E>()
            return try {
                with(context) { Ok(block()) }
            } catch (e: BindFailure) {
                context.error
            }
        }
    }
}
