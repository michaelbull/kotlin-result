package com.mikebull94.result

/**
 * - Elm: [Result.toMaybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#toMaybe)
 */
fun <V, E> Result<V, E>.get(): V? {
    return when (this) {
        is Ok -> value
        is Error -> null
    }
}

/**
 * - Elm: [Result.withDefault](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#withDefault)
 */
infix fun <V, E> Result<V, E>.getOrElse(default: V): V {
    return when (this) {
        is Ok -> value
        is Error -> default
    }
}
