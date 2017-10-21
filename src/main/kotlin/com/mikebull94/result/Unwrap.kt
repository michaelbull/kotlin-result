package com.mikebull94.result

class UnwrapException(message: String) : Exception(message)

/**
 * - Rust: [Result.unwrap](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap)
 */
fun <V, E> Result<V, E>.unwrap(): V {
    return when (this) {
        is Ok -> value
        is Error -> throw UnwrapException("called Result.wrap on an Error value $error")
    }
}

/**
 * - Rust: [Result.expect](https://doc.rust-lang.org/std/result/enum.Result.html#method.expect)
 */
infix fun <V, E> Result<V, E>.expect(message: String): V {
    return when (this) {
        is Ok -> value
        is Error -> throw UnwrapException("$message $error")
    }
}

/**
 * - Rust: [Result.unwrap_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_err)
 */
fun <V, E> Result<V, E>.unwrapError(): E {
    return when (this) {
        is Ok -> throw UnwrapException("called Result.unwrapError on an Ok value $value")
        is Error -> error
    }
}

/**
 * - Rust: [Reseult.expect_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.expect_err)
 */
infix fun <V, E> Result<V, E>.expectError(message: String): E {
    return when (this) {
        is Ok -> throw UnwrapException("$message $value")
        is Error -> error
    }
}
