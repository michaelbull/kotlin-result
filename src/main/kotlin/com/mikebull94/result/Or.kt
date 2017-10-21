package com.mikebull94.result

infix fun <V, E> Result<V, E>.or(default: V): Result<V, E> {
    return when (this) {
        is Ok -> this
        is Error -> ok(default)
    }
}

/**
 * - Elm: [Result.extract](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#extract)
 */
inline fun <V, E> Result<V, E>.extract(transform: (E) -> V): V {
    return when (this) {
        is Ok -> value
        is Error -> transform(error)
    }
}
