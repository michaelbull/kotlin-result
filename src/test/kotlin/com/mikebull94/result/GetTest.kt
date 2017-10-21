package com.mikebull94.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class GetTest {
    private object GetError

    @Test
    internal fun `get should return the result value if ok`() {
        val value = ok(12).get()
        assertThat(value, equalTo(12))
    }

    @Test
    internal fun `get should return null if not ok`() {
        val value = error(GetError).get()
        assertThat(value, equalTo(null))
    }

    @Test
    internal fun `getOrElse should return the result value if ok`() {
        val value = ok("hello").getOrElse("world")
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `getOrElse should return default value if not ok`() {
        val value = error(GetError).getOrElse("default")
        assertThat(value, equalTo("default"))
    }
}
