package com.github.michaelbull.result

class UnwrapException(message: String) : Exception(message)

/**
 * Unwraps a [Result], yielding the [value][Ok.value].
 *
 * - Rust: [Result.unwrap](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap)
 *
 * @throws UnwrapException if the [Result] is an [Err], with a message containing the [error][Err.error].
 */
fun <V, E> Result<V, E>.unwrap(): V {
    return when (this) {
        is Ok -> value
        is Err -> throw UnwrapException("called Result.wrap on an Err value $error")
    }
}

@Deprecated("Use lazy evaluation.", ReplaceWith("expect { message }"))
infix fun <V, E> Result<V, E>.expect(message: String): V {
    return expect { message }
}

/**
 * Unwraps a [Result], yielding the [value][Ok.value].
 *
 * - Rust: [Result.expect](https://doc.rust-lang.org/std/result/enum.Result.html#method.expect)
 *
 * @param message The message to include in the [UnwrapException] if the [Result] is an [Err].
 * @throws UnwrapException if the [Result] is an [Err], with the specified [message].
 */
infix inline fun <V, E> Result<V, E>.expect(message: () -> Any): V {
    return when (this) {
        is Ok -> value
        is Err -> throw UnwrapException("${message()} $error")
    }
}

/**
 * Unwraps a [Result], yielding the [error][Err.error].
 *
 * - Rust: [Result.unwrap_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_err)
 *
 * @throws UnwrapException if the [Result] is [Ok], with a message containing the [value][Ok.value].
 */
fun <V, E> Result<V, E>.unwrapError(): E {
    return when (this) {
        is Ok -> throw UnwrapException("called Result.unwrapError on an Ok value $value")
        is Err -> error
    }
}

@Deprecated("Use lazy evaluation.", ReplaceWith("expectError { message }"))
infix fun <V, E> Result<V, E>.expectError(message: String): E {
    return expectError { message }
}

/**
 * Unwraps a [Result], yielding the [error][Err.error].
 *
 * - Rust: [Result.expect_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.expect_err)
 *
 * @param message The message to include in the [UnwrapException] if the [Result] is [Ok].
 * @throws UnwrapException if the [Result] is [Ok], with the specified [message].
 */
infix inline fun <V, E> Result<V, E>.expectError(message: () -> Any): E {
    return when (this) {
        is Ok -> throw UnwrapException("${message()} $value")
        is Err -> error
    }
}
