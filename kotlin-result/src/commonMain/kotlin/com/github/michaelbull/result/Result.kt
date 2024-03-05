package com.github.michaelbull.result

/**
 * Returns a [Result] that [is ok][Result.isOk] and contains a [value][Result.value].
 */
@Suppress("FunctionName", "DEPRECATION")
public fun <V> Ok(value: V): Result<V, Nothing> {
    return Ok(value, null)
}

/**
 * Returns a [Result] that [is an error][Result.isErr] and contains an [error][Result.error].
 */
@Suppress("FunctionName", "DEPRECATION")
public fun <E> Err(error: E): Result<Nothing, E> {
    return Err(error, null)
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
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 * - Rust: [Result](https://doc.rust-lang.org/std/result/enum.Result.html)
 */
public sealed class Result<out V, out E> {

    public abstract val value: V
    public abstract val error: E

    public abstract val isOk: Boolean
    public abstract val isErr: Boolean

    public abstract operator fun component1(): V?
    public abstract operator fun component2(): E?
}

/**
 * Represents a successful [Result], containing a [value].
 */
@Deprecated(
    message = "Using Ok as a return type is deprecated.",
    replaceWith = ReplaceWith("Result<V, Nothing>"),
)
public class Ok<out V> internal constructor(
    override val value: V,
    @Suppress("UNUSED_PARAMETER") placeholder: Any?,
) : Result<V, Nothing>() {

    override val error: Nothing
        get() {
            throw NoSuchElementException()
        }

    override val isOk: Boolean = true
    override val isErr: Boolean = false

    override fun component1(): V = value
    override fun component2(): Nothing? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Ok<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "Ok($value)"
}

/**
 * Represents a failed [Result], containing an [error].
 */
@Deprecated(
    message = "Using Err as a return type is deprecated.",
    replaceWith = ReplaceWith("Result<Nothing, E>"),
)
public class Err<out E> internal constructor(
    override val error: E,
    @Suppress("UNUSED_PARAMETER") placeholder: Any?,
) : Result<Nothing, E>() {

    override val value: Nothing
        get() {
            throw NoSuchElementException()
        }

    override val isOk: Boolean = false
    override val isErr: Boolean = true

    override fun component1(): Nothing? = null
    override fun component2(): E = error

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Err<*>

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int = error.hashCode()
    override fun toString(): String = "Err($error)"
}
