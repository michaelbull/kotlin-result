package com.mikebull94.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class OrTest {
    private object OrError

    @Test
    internal fun `or should return the result value if ok`() {
        val value = ok(500).or(1000).get()
        assertThat(value, equalTo(500))
    }

    @Test
    internal fun `or should return the default value if not ok`() {
        val error = error(OrError).or(5000).get()
        assertThat(error, equalTo(5000))
    }

    @Test
    internal fun `extract should return the result value if ok`() {
        val value = ok("hello").extract { OrError } as String
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `extract should return the transformed result error if not ok`() {
        val error = error("hello").extract { "$it darkness" }
        assertThat(error, equalTo("hello darkness"))
    }
}
