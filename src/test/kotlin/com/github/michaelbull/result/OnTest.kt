package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

internal class OnTest {
    object CounterError
    class Counter(var count: Int)

    internal class `onSuccess` {
        @Test
        internal fun invokesActionIfOk() {
            val counter = Counter(50)

            Ok(counter).onSuccess { it.count += 50 }

            assertEquals(
                expected = 100,
                actual = counter.count
            )
        }

        @Test
        internal fun invokesNothingIfErr() {
            val counter = Counter(200)

            Err(CounterError).onSuccess { counter.count -= 50 }

            assertEquals(
                expected = 200,
                actual = counter.count
            )
        }
    }

    internal class `onFailure` {
        @Test
        internal fun invokesActionIfErr() {
            val counter = Counter(555)

            Err(CounterError).onFailure { counter.count += 100 }

            assertEquals(
                expected = 655,
                actual = counter.count
            )
        }

        @Test
        internal fun invokesNothingIfOk() {
            val counter = Counter(1020)

            Ok("hello").onFailure { counter.count = 1030 }

            assertEquals(
                expected = 1020,
                actual = counter.count
            )
        }
    }
}
