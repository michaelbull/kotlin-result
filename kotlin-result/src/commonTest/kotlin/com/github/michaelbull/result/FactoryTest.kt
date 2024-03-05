package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class FactoryTest {
    class RunCatching {

        @Test
        fun returnsOkIfInvocationSuccessful() {
            assertEquals(
                expected = Ok("example"),
                actual = runCatching { "example" },
            )
        }

        @Test
        @Suppress("UNREACHABLE_CODE")
        fun returnsErrIfInvocationFails() {
            val exception = IllegalArgumentException("throw me")

            assertEquals(
                expected = Err(exception),
                actual = runCatching { throw exception },
            )
        }
    }

    class ToResultOr {

        @Test
        fun returnsOkfIfNonNull() {
            assertEquals(
                expected = "ok",
                actual = "ok".toResultOr { "err" }.get()
            )
        }

        @Test
        fun returnsErrIfNull() {
            assertEquals(
                expected = Err("err"),
                actual = "ok".toLongOrNull().toResultOr { "err" }
            )
        }
    }
}
