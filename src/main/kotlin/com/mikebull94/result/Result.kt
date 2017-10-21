package com.mikebull94.result

/**
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 */
sealed class Result<out V, out E>

fun <V> ok(value: V) = Ok<V, Nothing>(value)
fun <E> err(error: E) = Error<Nothing, E>(error)

class Ok<out V, out E> internal constructor(val value: V) : Result<V, E>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ok<*, *>

        if (value != other.value) return false

        return true
    }

    override fun hashCode() = value?.hashCode() ?: 0
    override fun toString() = "Result.Ok($value)"
}

class Error<out V, out E> internal constructor(val error: E) : Result<V, E>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Error<*, *>

        if (error != other.error) return false

        return true
    }

    override fun hashCode() = error?.hashCode() ?: 0
    override fun toString() = "Result.Error($error)"
}
