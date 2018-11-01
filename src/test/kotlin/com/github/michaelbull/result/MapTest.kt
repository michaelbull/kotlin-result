package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MapTest {
    private sealed class MapErr(val reason: String) {
        object HelloError : MapErr("hello")
        object WorldError : MapErr("world")
        class CustomError(reason: String) : MapErr(reason)
    }

    class Map {
        @Test
        fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = 30,
                actual = Ok(10).map { it + 20 }.get()
            )
        }

        @Test
        @Suppress("UNREACHABLE_CODE")
        fun returnsErrorIfErr() {
            val result = Err(MapErr.HelloError).map { "hello $it" }

            result as Err

            assertSame(
                expected = MapErr.HelloError,
                actual = result.error
            )
        }
    }

    class MapError {
        @Test
        fun returnsValueIfOk() {
            val value = Ok(55).map { it + 15 }.mapError { MapErr.WorldError }.get()

            assertEquals(
                expected = 70,
                actual = value
            )
        }

        @Test
        fun returnsErrorIfErr() {
            val result: Result<String, MapErr> = Ok("let")
                .map { "$it me" }
                .andThen {
                    when (it) {
                        "let me" -> Err(MapErr.CustomError("$it $it"))
                        else -> Ok("$it get")
                    }
                }
                .mapError { MapErr.CustomError("${it.reason} get what i want") }

            result as Err

            assertEquals(
                expected = "let me let me get what i want",
                actual = result.error.reason
            )
        }
    }

    class MapBoth {
        @Test
        @Suppress("UNREACHABLE_CODE")
        fun returnsTransformedValueIfOk() {
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
        fun returnsTransformedErrorIfErr() {
            val error = Err(MapErr.CustomError("this")).mapBoth(
                success = { "$it charming" },
                failure = { "${it.reason} man" }
            )

            assertEquals(
                expected = "this man",
                actual = error
            )
        }
    }

    class MapEither {
        @Test
        @Suppress("UNREACHABLE_CODE")
        fun returnsTransformedValueIfOk() {
            val result = Ok(500).mapEither(
                success = { it + 500 },
                failure = { MapErr.CustomError("$it") }
            )

            result as Ok

            assertEquals(
                expected = 1000,
                actual = result.value
            )
        }

        @Test
        fun returnsTransformedErrorIfErr() {
            val result = Err("the reckless").mapEither(
                success = { "the wild youth" },
                failure = { MapErr.CustomError("the truth") }
            )

            result as Err

            assertEquals(
                expected = "the truth",
                actual = result.error.reason
            )
        }
    }
}
