package com.github.michaelbull.result

/**
 * Returns the [value][Ok.value] if this [Result] is [Ok], otherwise `null`.
 *
 * - Elm: [Result.toMaybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#toMaybe)
 * - Rust: [Result.ok](https://doc.rust-lang.org/std/result/enum.Result.html#method.ok)
 *
 * @return The [value][Ok.value] if [Ok], otherwise `null`.
 */
fun <V, E> Result<V, E>.get(): V? {
    return when (this) {
        is Ok -> value
        is Err -> null
    }
}

/**
 * Returns the [error][Err.error] if this [Result] is [Err], otherwise `null`.
 *
 * - Rust: [Result.err](https://doc.rust-lang.org/std/result/enum.Result.html#method.err)
 *
 * @return The [error][Err.error] if [Err], otherwise `null`.
 */
fun <V, E> Result<V, E>.getError(): E? {
    return when (this) {
        is Ok -> null
        is Err -> error
    }
}

@Deprecated("Use lazy-evaluating variant instead", ReplaceWith("getOr { default }"))
infix fun <V, E> Result<V, E>.getOr(default: V): V {
    return getOr { default }
}

/**
 * Returns the [value][Ok.value] if this [Result] is [Ok], otherwise [default].
 *
 * - Elm: [Result.withDefault](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#withDefault)
 * - Haskell: [Result.fromLeft](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromLeft)
 * - Rust: [Result.unwrap_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_or)
 *
 * @param default The value to return if [Err].
 * @return The [value][Ok.value] if [Ok], otherwise [default].
 */
infix inline fun <V, E> Result<V, E>.getOr(default: () -> V): V {
    return when (this) {
        is Ok -> value
        is Err -> default()
    }
}

@Deprecated("Use lazy-evaluating variant instead", ReplaceWith("getErrorOr { default }"))
infix fun <V, E> Result<V, E>.getErrorOr(default: E): E {
    return getErrorOr { default }
}

/**
 * Returns the [error][Err.error] if this [Result] is [Err], otherwise [default].
 *
 * - Haskell: [Result.fromRight](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromRight)
 *
 * @param default The error to return if [Ok].
 * @return The [error][Err.error] if [Err], otherwise [default].
 */
infix inline fun <V, E> Result<V, E>.getErrorOr(default: () -> E): E {
    return when (this) {
        is Ok -> default()
        is Err -> error
    }
}

/**
 * Returns the [value][Ok.value] if this [Result] is [Ok], otherwise
 * the [transformation][transform] of the [error][Err.error].
 *
 * - Elm: [Result.extract](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#extract)
 * - Rust: [Result.unwrap_or_else](https://doc.rust-lang.org/src/core/result.rs.html#735-740)
 *
 * @param transform The transformation to apply to the [error][Err.error].
 * @return The [value][Ok.value] if [Ok], otherwise the [transformed][transform] [error][Err.error].
 */
infix inline fun <V, E> Result<V, E>.getOrElse(transform: (E) -> V): V {
    return when (this) {
        is Ok -> value
        is Err -> transform(error)
    }
}

/**
 * Returns the [error][Err.error] if this [Result] is [Err], otherwise
 * the [transformation][transform] of the [value][Ok.value].
 *
 * @param transform The transformation to apply to the [value][Ok.value].
 * @return The [error][Err.error] if [Err], otherwise the [transformed][transform] [value][Ok.value].
 */
infix inline fun <V, E> Result<V, E>.getErrorOrElse(transform: (V) -> E): E {
    return when (this) {
        is Ok -> transform(value)
        is Err -> error
    }
}
