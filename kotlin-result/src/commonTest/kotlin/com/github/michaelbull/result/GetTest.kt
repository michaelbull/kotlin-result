package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
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
                actual = Ok("hello").getOr("world")
            )
        }

        @Test
        fun returnsDefaultValueIfErr() {
            assertEquals(
                expected = "default",
                actual = Err("error").getOr("default")
            )
        }
    }

    class GetOrThrow {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").getOrThrow()
            )
        }

        @Test
        fun throwsErrorIfErr() {
            assertFailsWith<CustomException> {
                Err(CustomException()).getOrThrow()
            }
        }

        class CustomException : Throwable()
    }

    class GetOrThrowWithTransform {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").getOrThrow { CustomException("Failed") }
            )
        }

        @Test
        fun throwsTransformedErrorIfErr() {
            assertFailsWith<CustomException> {
                Err("error").getOrThrow { error -> CustomException(error) }
            }
        }

        class CustomException(message: String) : Throwable(message)
    }

    class GetErrorOr {
        @Test
        fun returnsDefaultValueIfOk() {
            assertEquals(
                expected = "world",
                actual = Ok("hello").getErrorOr("world")
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = "hello",
                actual = Err("hello").getErrorOr("world")
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
                expected = setOf(4, 5, 6),
                actual = Ok(listOf(1, 2, 3)).and(Ok(setOf(4, 5, 6))).merge()
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = listOf(1, 2, 3),
                actual = Err(listOf(1, 2, 3)).and(Err(setOf(4, 5, 6))).merge()
            )
        }
    }
}
