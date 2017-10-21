package com.mikebull94.result

/**
 * - Elm: [Result.toMaybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#toMaybe)
 * - Rust: [Result.ok](https://doc.rust-lang.org/std/result/enum.Result.html#method.ok)
 */
fun <V, E> Result<V, E>.get(): V? {
    return when (this) {
        is Ok -> value
        is Error -> null
    }
}

/**
 * - Rust: [Result.err](https://doc.rust-lang.org/std/result/enum.Result.html#method.err)
 */
fun <V, E> Result<V, E>.getError(): E? {
    return when(this) {
        is Ok -> null
        is Error -> error
    }
}

/**
 * - Elm: [Result.withDefault](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#withDefault)
 * - Rust: [Result.unwrap_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_or)
 */
infix fun <V, E> Result<V, E>.getOr(default: V): V {
    return when (this) {
        is Ok -> value
        is Error -> default
    }
}

/**
 * - Elm: [Result.extract](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#extract)
 * - Rust: [Result.unwrap_or_else](https://doc.rust-lang.org/src/core/result.rs.html#735-740)
 */
infix inline fun <V,E> Result<V,E>.getOrElse(transform: (E) -> V): V {
    return when (this) {
        is Ok -> value
        is Error -> transform(error)
    }
}
