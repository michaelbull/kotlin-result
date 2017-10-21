package com.github.michaelbull.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.jupiter.api.Test

internal class AndTest {
    private object AndError

    @Test
    internal fun `and should return the result value if ok`() {
        val value = ok(230).and(ok(500)).get()
        assertThat(value, equalTo(500))
    }

    @Test
    internal fun `and should return the result value if not ok`() {
        val error = ok(300).and(err("hello world")).getError()
        assertThat(error, equalTo("hello world"))
    }

    @Test
    internal fun `andThen should return the transformed result value if ok`() {
        val value = ok(5).andThen { ok(it + 7) }.get()
        assertThat(value, equalTo(12))
    }

    @Test
    internal fun `andThen should return the result error if not ok`() {
        val error = ok(20).andThen { ok(it + 43) }.andThen { err(AndError) }.getError()!!
        assertThat(error, sameInstance(AndError))
    }
}
