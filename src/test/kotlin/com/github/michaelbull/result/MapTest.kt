package com.github.michaelbull.result

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.jupiter.api.Test

internal class MapTest {
    private sealed class MapError(val reason: String) {
        object HelloError : MapError("hello")
        object WorldError : MapError("world")
        class CustomError(reason: String) : MapError(reason)
    }

    private fun sameError(error: MapError): Matcher<MapError> {
        return sameInstance(error)
    }

    @Test
    internal fun `map should return the transformed result value if ok`() {
        val test: Result<Int, Nothing> = Ok(500).map { 50 }

        val value = Ok(10).map { it + 20 }.get()
        assertThat(value, equalTo(30))
    }

    @Test
    internal fun `map should return the result error if not ok`() {
        val result = Error(MapError.HelloError).map { "hello $it" }

        result as Error

        assertThat(result.error, sameError(MapError.HelloError))
    }

    @Test
    internal fun `mapError should return the result value if ok`() {
        val value = Ok(55).map { it + 15 }.mapError { MapError.WorldError }.get()
        assertThat(value, equalTo(70))
    }

    @Test
    internal fun `mapError should return the transformed result error if not ok`() {
        val result: Result<String, MapError> = Ok("let")
            .map { "$it me" }
            .andThen {
                when (it) {
                    "let me" -> Error(MapError.CustomError("$it $it"))
                    else -> Ok("$it get")
                }
            }
            .mapError { MapError.CustomError("${it.reason} get what i want") }

        result as Error

        assertThat(result.error.reason, equalTo("let me let me get what i want"))
    }

    @Test
    internal fun `mapBoth should return the transformed result value if ok`() {
        val value = Ok("there is").mapBoth(
            success = { "$it a light" },
            failure = { "$it that never" }
        )

        assertThat(value, equalTo("there is a light"))
    }

    @Test
    internal fun `mapBoth should return the transformed result error if not ok`() {
        val error = Error(MapError.CustomError("this")).mapBoth(
            success = { "$it charming" },
            failure = { "${it.reason} man" }
        )

        assertThat(error, equalTo("this man"))
    }

    @Test
    internal fun `mapEither should return the transformed result value if ok`() {
        val result = Ok(500).mapEither(
            success = { it + 500 },
            failure = { MapError.CustomError("$it") }
        )

        result as Ok

        assertThat(result.value, equalTo(1000))
    }

    @Test
    internal fun `mapEither should return the transformed result error if not ok`() {
        val result = Error("the reckless").mapEither(
            success = { "the wild youth" },
            failure = { MapError.CustomError("the truth") }
        )

        result as Error

        assertThat(result.error.reason, equalTo("the truth"))
    }
}
