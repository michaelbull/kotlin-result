package com.mikebull94.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class GetTest {
    @Test
    internal fun `get should return the result value if ok`() {
        val value = ok(12).get()
        assertThat(value, equalTo(12))
    }

    @Test
    internal fun `get should return null if not ok`() {
        val value = err("error").get()
        assertThat(value, equalTo(null))
    }

    @Test
    internal fun `getError should return null if ok`() {
        val error = ok("example").getError()
        assertThat(error, equalTo(null))
    }

    @Test
    internal fun `getError should return the result error if not ok`() {
        val error = err("example").getError()
        assertThat(error, equalTo("example"))
    }

    @Test
    internal fun `getOr should return the result value if ok`() {
        val value = ok("hello").getOr("world")
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `getOr should return default value if not ok`() {
        val value = err("error").getOr("default")
        assertThat(value, equalTo("default"))
    }

    @Test
    internal fun `getErrorOr should return the default value if ok`() {
        val error = ok("hello").getErrorOr("world")
        assertThat(error, equalTo("world"))
    }

    @Test
    internal fun `getErrorOr should return the result error if not ok`() {
        val error = err("hello").getErrorOr("world")
        assertThat(error, equalTo("hello"))
    }

    @Test
    internal fun `getOrElse should return the result value if ok`() {
        val value = ok("hello").getOrElse { "world" }
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `getOrElse should return the transformed result error if ok`() {
        val value = err("hello").getOrElse { "world" }
        assertThat(value, equalTo("world"))
    }
}
