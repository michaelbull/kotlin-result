package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class ResultTest {
    internal class `of` {
        @Test
        internal fun returnsOkIfInvocationSuccessful() {
            val callback = { "example" }
            val result = Result.of(callback)

            assertEquals(
                expected = "example",
                actual = result.get()
            )
        }

        @Test
        internal fun returnsErrIfInvocationFails() {
            val exception = IllegalArgumentException("throw me")
            val callback = { throw exception }
            val result = Result.of(callback)

            assertSame(
                expected = exception,
                actual = result.getError()
            )
        }
    }

    internal class `toResultOr` {
        @Test
        internal fun returnsOkfIfNonNull() {
            val result = "ok".toResultOr { "err" }

            assertEquals(
                expected = "ok",
                actual = result.get()
            )
        }

        @Test
        internal fun returnsErrIfNull() {
            val result = "ok".toLongOrNull().toResultOr { "err" }

            assertEquals(
                expected = "err",
                actual = result.getError()
            )
        }
    }
}
