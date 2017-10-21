package com.mikebull94.result

/**
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 */
infix fun <V, E> Result<V, E>.or(result: Result<V, E>): Result<V, E> {
    return when(this) {
        is Ok -> ok(value)
        is Error -> result
    }
}

/**
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 */
infix inline fun <V, E> Result<V, E>.orElse(transform: (E) -> Result<V, E>): Result<V, E> {
    return when (this) {
        is Ok -> ok(this.value)
        is Error -> transform(error)
    }
}
