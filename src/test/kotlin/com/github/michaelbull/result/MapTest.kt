package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class MapTest {
    private sealed class MapError(val reason: String) {
        object HelloError : MapError("hello")
        object WorldError : MapError("world")
        class CustomError(reason: String) : MapError(reason)
    }

    internal class `map` {
        @Test
        internal fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = 30,
                actual = Ok(10).map { it + 20 }.get()
            )
        }

        @Test
        @Suppress("UNREACHABLE_CODE")
        internal fun returnsErrorIfErr() {
            val result = Err(MapError.HelloError).map { "hello $it" }

            result as Err

            assertSame(
                expected = MapError.HelloError,
                actual = result.error
            )
        }
    }

    internal class `mapError` {
        @Test
        internal fun returnsValueIfOk() {
            val value = Ok(55).map { it + 15 }.mapError { MapError.WorldError }.get()

            assertEquals(
                expected = 70,
                actual = value
            )
        }

        @Test
        internal fun returnsErrorIfErr() {
            val result: Result<String, MapError> = Ok("let")
                .map { "$it me" }
                .andThen {
                    when (it) {
                        "let me" -> Err(MapError.CustomError("$it $it"))
                        else -> Ok("$it get")
                    }
                }
                .mapError { MapError.CustomError("${it.reason} get what i want") }

            result as Err

            assertEquals(
                expected = "let me let me get what i want",
                actual = result.error.reason
            )
        }
    }

    internal class `mapBoth` {
        @Test
        @Suppress("UNREACHABLE_CODE")
        internal fun returnsTransformedValueIfOk() {
            val value = Ok("there is").mapBoth(
                success = { "$it a light" },
                failure = { "$it that never" }
            )

            assertEquals(
                expected = "there is a light",
                actual = value
            )
        }

        @Test
        @Suppress("UNREACHABLE_CODE")
        internal fun returnsTransformedErrorIfErr() {
            val error = Err(MapError.CustomError("this")).mapBoth(
                success = { "$it charming" },
                failure = { "${it.reason} man" }
            )

            assertEquals(
                expected = "this man",
                actual = error
            )
        }
    }

    internal class `mapEither` {
        @Test
        @Suppress("UNREACHABLE_CODE")
        internal fun returnsTransformedValueIfOk() {
            val result = Ok(500).mapEither(
                success = { it + 500 },
                failure = { MapError.CustomError("$it") }
            )

            result as Ok

            assertEquals(
                expected = 1000,
                actual = result.value
            )
        }

        @Test
        internal fun returnsTransformedErrorIfErr() {
            val result = Err("the reckless").mapEither(
                success = { "the wild youth" },
                failure = { MapError.CustomError("the truth") }
            )

            result as Err

            assertEquals(
                expected = "the truth",
                actual = result.error.reason
            )
        }
    }
}
