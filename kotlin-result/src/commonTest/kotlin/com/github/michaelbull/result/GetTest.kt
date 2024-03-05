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
                Err("error").getOrThrow(::CustomException)
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
        interface Direction
        object Left : Direction
        object Right : Direction

        @Test
        fun returnsValueIfOk() {
            val left: Result<Left, Left> = Ok(Left)
            val right: Result<Right, Right> = Ok(Right)

            val result: Result<Direction, Direction> = left.flatMapEither(
                success = { left },
                failure = { right },
            )

            val direction: Direction = result.merge()

            assertEquals(
                expected = Left,
                actual = direction
            )
        }

        @Test
        fun returnsErrorIfErr() {
            val left: Result<Left, Left> = Err(Left)
            val right: Result<Right, Right> = Err(Right)

            val result: Result<Direction, Direction> = left.flatMapEither(
                success = { left },
                failure = { right },
            )

            val direction: Direction = result.merge()

            assertEquals(
                expected = Right,
                actual = direction
            )
        }
    }
}
