package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetTest {
    class Get {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 12,
                actual = Ok(12).get()
            )
        }

        @Test
        fun returnsNullIfErr() {
            assertNull(Err("error").get())
        }
    }

    class GetError {
        @Test
        fun returnsNullIfOk() {
            assertNull(Ok("example").getError())
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = "example",
                actual = Err("example").getError()
            )
        }
    }

    class GetOr {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").getOr { "world" }
            )
        }

        @Test
        fun returnsDefaultValueIfErr() {
            assertEquals(
                expected = "default",
                actual = Err("error").getOr { "default" }
            )
        }
    }

    class GetErrorOr {
        @Test
        fun returnsDefaultValueIfOk() {
            assertEquals(
                expected = "world",
                actual = Ok("hello").getErrorOr { "world" }
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = "hello",
                actual = Err("hello").getErrorOr { "world" }
            )
        }
    }

    class GetOrElse {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").getOrElse { "world" }
            )
        }

        @Test
        fun returnsTransformedErrorIfErr() {
            assertEquals(
                expected = "world",
                actual = Err("hello").getOrElse { "world" }
            )
        }
    }

    class GetErrorOrElse {
        @Test
        fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = "world",
                actual = Ok("hello").getErrorOrElse { "world" }
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = "hello",
                actual = Err("hello").getErrorOrElse { "world" }
            )
        }
    }

    class Merge {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = listOf(1, 2, 3),
                actual = Ok(listOf(1, 2, 3)).merge()
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = setOf(4, 5, 6),
                actual = Err(setOf(4, 5, 6)).merge()
            )
        }
    }
}
