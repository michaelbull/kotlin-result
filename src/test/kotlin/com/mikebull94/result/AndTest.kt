package com.mikebull94.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.jupiter.api.Test

internal class AndTest {
    private object AndError

    @Test
    internal fun `andThen should return the transformed result value if ok`() {
        val value = ok(5).andThen { ok(it + 7) }.get()
        assertThat(value, equalTo(12))
    }

    @Test
    internal fun `andThen should return the result error if not ok`() {
        val result = ok(20).andThen { ok(it + 43) }.andThen { error(AndError) }

        result as Error

        assertThat(result.error, sameInstance(AndError))
    }
}
