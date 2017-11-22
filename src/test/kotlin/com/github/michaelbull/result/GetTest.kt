package com.github.michaelbull.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class GetTest {
    @Test
    internal fun `get should return the result value if ok`() {
        val value = Ok(12).get()
        assertThat(value, equalTo(12))
    }

    @Test
    internal fun `get should return null if not ok`() {
        val value = Err("error").get()
        assertThat(value, equalTo(null))
    }

    @Test
    internal fun `getError should return null if ok`() {
        val error = Ok("example").getError()
        assertThat(error, equalTo(null))
    }

    @Test
    internal fun `getError should return the result error if not ok`() {
        val error = Err("example").getError()
        assertThat(error, equalTo("example"))
    }

    @Test
    internal fun `getOr should return the result value if ok`() {
        val value = Ok("hello").getOr("world")
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `getOr should return default value if not ok`() {
        val value = Err("error").getOr("default")
        assertThat(value, equalTo("default"))
    }

    @Test
    internal fun `getErrorOr should return the default value if ok`() {
        val error = Ok("hello").getErrorOr("world")
        assertThat(error, equalTo("world"))
    }

    @Test
    internal fun `getErrorOr should return the result error if not ok`() {
        val error = Err("hello").getErrorOr("world")
        assertThat(error, equalTo("hello"))
    }

    @Test
    internal fun `getOrElse should return the result value if ok`() {
        val value = Ok("hello").getOrElse { "world" }
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `getOrElse should return the transformed result error if ok`() {
        val value = Err("hello").getOrElse { "world" }
        assertThat(value, equalTo("world"))
    }

    @Test
    internal fun `getErrorOrElse should return the transformed result value if ok`() {
        val error = Ok("hello").getErrorOrElse { "world" }
        assertThat(error, equalTo("world"))
    }

    @Test
    internal fun `getErrorOrElse should return the result error if not ok`() {
        val error = Err("hello").getErrorOrElse { "world" }
        assertThat(error, equalTo("hello"))
    }
}
