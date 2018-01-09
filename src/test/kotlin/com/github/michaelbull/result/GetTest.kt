package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GetTest {
    internal class `get` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = 12,
                actual = Ok(12).get()
            )
        }

        @Test
        internal fun returnsNullIfErr() {
            assertNull(Err("error").get())
        }
    }

    internal class `getError` {
        @Test
        internal fun returnsNullIfOk() {
            assertNull(Ok("example").getError())
        }

        @Test
        internal fun returnsErrorIfErr() {
            assertEquals(
                expected = "example",
                actual = Err("example").getError()
            )
        }
    }

    internal class `getOr` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").getOr { "world" }
            )
        }

        @Test
        internal fun returnsDefaultValueIfErr() {
            assertEquals(
                expected = "default",
                actual = Err("error").getOr { "default" }
            )
        }
    }

    internal class `getErrorOr` {
        @Test
        internal fun returnsDefaultValueIfOk() {
            assertEquals(
                expected = "world",
                actual = Ok("hello").getErrorOr { "world" }
            )
        }

        @Test
        internal fun returnsErrorIfErr() {
            assertEquals(
                expected = "hello",
                actual = Err("hello").getErrorOr { "world" }
            )
        }
    }

    internal class `getOrElse` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").getOrElse { "world" }
            )
        }

        @Test
        internal fun returnsTransformedErrorIfErr() {
            assertEquals(
                expected = "world",
                actual = Err("hello").getOrElse { "world" }
            )
        }
    }

    internal class `getErrorOrElse` {
        @Test
        internal fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = "world",
                actual = Ok("hello").getErrorOrElse { "world" }
            )
        }

        @Test
        internal fun returnsErrorIfErr() {
            assertEquals(
                expected = "hello",
                actual = Err("hello").getErrorOrElse { "world" }
            )
        }
    }
}
