package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class OnTest {
    object CounterError
    class Counter(var count: Int)

    class OnOk {

        @Test
        fun invokesActionIfOk() {
            val counter = Counter(50)

            Ok(counter).onOk { it.count += 50 }

            assertEquals(
                expected = 100,
                actual = counter.count
            )
        }

        @Test
        fun invokesNothingIfErr() {
            val counter = Counter(200)

            Err(CounterError).onOk { counter.count -= 50 }

            assertEquals(
                expected = 200,
                actual = counter.count
            )
        }
    }

    class OnErr {

        @Test
        fun invokesActionIfErr() {
            val counter = Counter(555)

            Err(CounterError).onErr { counter.count += 100 }

            assertEquals(
                expected = 655,
                actual = counter.count
            )
        }

        @Test
        fun invokesNothingIfOk() {
            val counter = Counter(1020)

            Ok("hello").onErr { counter.count = 1030 }

            assertEquals(
                expected = 1020,
                actual = counter.count
            )
        }
    }
}
