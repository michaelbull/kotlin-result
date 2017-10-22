package com.github.michaelbull.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.jupiter.api.Test

internal class AndTest {
    private object AndError

    @Test
    internal fun `and should return the result value if ok`() {
        val value = Ok(230).and(Ok(500)).get()
        assertThat(value, equalTo(500))
    }

    @Test
    internal fun `and should return the result value if not ok`() {
        val error = Ok(300).and(Error("hello world")).getError()
        assertThat(error, equalTo("hello world"))
    }

    @Test
    internal fun `andThen should return the transformed result value if ok`() {
        val value = Ok(5).andThen { Ok(it + 7) }.get()
        assertThat(value, equalTo(12))
    }

    @Test
    internal fun `andThen should return the result error if not ok`() {
        val error = Ok(20).andThen { Ok(it + 43) }.andThen { Error(AndError) }.getError()!!
        assertThat(error, sameInstance(AndError))
    }
}
