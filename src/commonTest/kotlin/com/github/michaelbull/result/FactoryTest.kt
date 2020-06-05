package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FactoryTest {
    class RunCatching {
        @Test
        fun returnsOkIfInvocationSuccessful() {
            val callback = { "example" }
            val result = runCatching(callback)

            assertEquals(
                expected = "example",
                actual = result.get()
            )
        }

        @Test
        fun returnsErrIfInvocationFails() {
            val exception = IllegalArgumentException("throw me")
            val callback = { throw exception }
            val result = runCatching(callback)

            assertSame(
                expected = exception,
                actual = result.getError()
            )
        }
    }

    class ToResultOr {
        @Test
        fun returnsOkfIfNonNull() {
            val result = "ok".toResultOr { "err" }

            assertEquals(
                expected = "ok",
                actual = result.get()
            )
        }

        @Test
        fun returnsErrIfNull() {
            val result = "ok".toLongOrNull().toResultOr { "err" }

            assertEquals(
                expected = "err",
                actual = result.getError()
            )
        }
    }
}
