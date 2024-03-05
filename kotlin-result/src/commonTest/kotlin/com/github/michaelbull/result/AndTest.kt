package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class AndTest {
    private object AndError

    class And {

        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(500),
                actual = Ok(230).and(Ok(500)),
            )
        }

        @Test
        fun returnsValueIfErr() {
            assertEquals(
                expected = Err("hello world"),
                actual = Ok(300).and(Err("hello world")),
            )
        }
    }

    class AndThen {

        @Test
        fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = Ok(12),
                actual = Ok(5).andThen { Ok(it + 7) },
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = Err(AndError),
                actual = Ok(20).andThen { Ok(it + 43) }.andThen { Err(AndError) },
            )
        }
    }
}
