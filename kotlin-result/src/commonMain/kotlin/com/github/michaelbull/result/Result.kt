package com.github.michaelbull.result

/**
 * [Result] is a type that represents either success ([Ok]) or failure ([Err]).
 *
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 * - Rust: [Result](https://doc.rust-lang.org/std/result/enum.Result.html)
 */
public sealed class Result<out V, out E> {

    public abstract operator fun component1(): V?
    public abstract operator fun component2(): E?

    public companion object {

        /**
         * Invokes a [function] and wraps it in a [Result], returning an [Err]
         * if an [Exception] was thrown, otherwise [Ok].
         */
        @Deprecated("Use top-level runCatching instead", ReplaceWith("runCatching(function)"))
        public inline fun <V> of(function: () -> V): Result<V, Exception> {
            return try {
                Ok(function.invoke())
            } catch (ex: Exception) {
                Err(ex)
            }
        }
    }
}

/**
 * Represents a successful [Result], containing a [value].
 */
public class Ok<out V>(public val value: V) : Result<V, Nothing>() {

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
public class Err<out E>(public val error: E) : Result<Nothing, E>() {

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
