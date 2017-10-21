package com.mikebull94.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class OnTest {
    object CounterError
    class Counter(var count: Int)

    @Test
    internal fun `onSuccess should invoke the callback when result is ok`() {
        val counter = Counter(50)
        ok(counter).onSuccess { it.count += 50 }
        assertThat(counter.count, equalTo(100))
    }

    @Test
    internal fun `onSuccess should not invoke the callback when result is not ok`() {
        val counter = Counter(200)
        err(CounterError).onSuccess { counter.count -= 50 }
        assertThat(counter.count, equalTo(200))
    }

    @Test
    internal fun `onFailure should invoke the callback when result is not ok`() {
        val counter = Counter(555)
        err(CounterError).onFailure { counter.count += 100 }
        assertThat(counter.count, equalTo(655))
    }

    @Test
    internal fun `onFailure should not invoke the callback when result is ok`() {
        val counter = Counter(1020)
        ok("hello").onFailure { counter.count = 1030 }
        assertThat(counter.count, equalTo(1020))
    }
}
