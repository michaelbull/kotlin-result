package com.mikebull94.result

/**
 * - Elm: [Result.andThen](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#andThen)
 */
inline fun <V, E, U> Result<V, E>.andThen(transform: (V) -> Result<U, E>): Result<U, E> {
    return when (this) {
        is Ok -> transform(value)
        is Error -> err(error)
    }
}
