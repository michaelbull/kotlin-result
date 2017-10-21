package com.github.michaelbull.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class OrTest {
    private object OrError

    @Test
    internal fun `or should return the result value if ok`() {
        val value = ok(500).or(ok(1000)).get()
        assertThat(value, equalTo(500))
    }

    @Test
    internal fun `or should return the default value if not ok`() {
        val value = err(OrError).or(ok(5000)).get()
        assertThat(value, equalTo(5000))
    }

    @Test
    internal fun `orElse should return the result value if ok`() {
        val value = ok(3000).orElse { ok(4000) }.get()
        assertThat(value, equalTo(3000))
    }

    @Test
    internal fun `orElse should return the transformed value if not ok`() {
        val value = err(4000).orElse { ok(2000) }.get()
        assertThat(value, equalTo(2000))
    }
}
