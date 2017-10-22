package com.github.michaelbull.result

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.jupiter.api.Test

internal class ResultTest {
    @Test
    internal fun `of should return ok if invocation did not throw anything`() {
        val callback = { "example" }
        val value = Result.of(callback).get()
        assertThat(value, equalTo("example"))
    }

    @Test
    internal fun `of should return error if invocation threw something`() {
        val throwable = IllegalArgumentException("throw me")
        val callback = { throw throwable }
        val error = Result.of(callback).getError()!!
        val matcher: Matcher<Throwable> = sameInstance(throwable)
        assertThat(error, matcher)
    }
}
