package com.github.michaelbull.result

import com.github.michaelbull.result.annotation.UnsafeResultErrorAccess
import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import kotlin.jvm.JvmInline

/**
 * Returns a [Result] that [is ok][Result.isOk] and contains a [value][Result.value].
 */
@Suppress("FunctionName")
public fun <V> Ok(value: V): Result<V, Nothing> {
    return Result(value)
}

/**
 * Returns a [Result] that [is an error][Result.isErr] and contains an [error][Result.error].
 */
@Suppress("FunctionName")
public fun <E> Err(error: E): Result<Nothing, E> {
    return Result(Failure(error))
}

/**
 * Unsafely casts this [Result<V, E>][Result] to [Result<U, Nothing>][Result], to be used inside
 * an explicit [isOk][Result.isOk] or [isErr][Result.isErr] guard.
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
public inline fun <V, E, U> Result<V, E>.asOk(): Result<U, Nothing> {
    return this as Result<U, Nothing>
}

/**
 * Unsafely casts this [Result<V, E>][Result] to [Result<Nothing, F>][Result], to be used inside
 * an explicit [isOk][Result.isOk] or [isErr][Result.isErr] guard.
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
public inline fun <V, E, F> Result<V, E>.asErr(): Result<Nothing, F> {
    return this as Result<Nothing, F>
}

/**
 * [Result] is a type that represents either success ([Ok]) or failure ([Err]).
 *
 * A [Result] that [is ok][Result.isOk] will have a [value][Result.value] of type [V], whereas a
 * [Result] that [is an error][Result.isErr] will have an [error][Result.error] of type [E].
 *
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 * - Rust: [Result](https://doc.rust-lang.org/std/result/enum.Result.html)
 */
@JvmInline
public value class Result<out V, out E> internal constructor(
    private val inlineValue: Any?,
) {

    @Suppress("UNCHECKED_CAST")
    @UnsafeResultValueAccess
    public val value: V
        get() = inlineValue as V

    @Suppress("UNCHECKED_CAST")
    @UnsafeResultErrorAccess
    public val error: E
        get() = (inlineValue as Failure<E>).error

    public val isOk: Boolean
        get() = inlineValue !is Failure<*>

    public val isErr: Boolean
        get() = inlineValue is Failure<*>

    public operator fun component1(): V? {
        return when {
            isOk -> value
            else -> null
        }
    }

    public operator fun component2(): E? {
        return when {
            isErr -> error
            else -> null
        }
    }

    override fun toString(): String {
        return when {
            isOk -> "Ok($value)"
            else -> "Err($error)"
        }
    }
}

private class Failure<out E>(
    val error: E,
) {
    override fun equals(other: Any?): Boolean {
        return other is Failure<*> && error == other.error
    }

    override fun hashCode(): Int {
        return error.hashCode()
    }

    override fun toString(): String {
        return "Failure($error)"
    }
}
