package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AndTest {
    private object AndError

    class And {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 500,
                actual = Ok(230).and(Ok(500)).get()
            )
        }

        @Test
        fun returnsValueIfErr() {
            assertEquals(
                expected = "hello world",
                actual = Ok(300).and(Err("hello world")).getError()
            )
        }
    }

    class AndThen {
        @Test
        fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = 12,
                actual = Ok(5).andThen { Ok(it + 7) }.get()
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertSame(
                expected = AndError,
                actual = Ok(20).andThen { Ok(it + 43) }.andThen { Err(AndError) }.getError()!!
            )
        }
    }
}
