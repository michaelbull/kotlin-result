package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class OrTest {
    private object OrError

    class Or {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 500,
                actual = Ok(500).or(Ok(1000)).get()
            )
        }

        @Test
        fun returnsDefaultValueIfErr() {
            assertEquals(
                expected = 5000,
                actual = Err(OrError).or(Ok(5000)).get()
            )
        }
    }

    class OrElse {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 3000,
                actual = Ok(3000).orElse { Ok(4000) }.get()
            )
        }

        @Test
        fun returnsTransformedValueIfErr() {
            assertEquals(
                expected = 2000,
                actual = Err(4000).orElse { Ok(2000) }.get()
            )
        }
    }

    class Recover {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 3000,
                actual = Ok(3000).recover { 4000 }.get()
            )
        }

        @Test
        fun returnsTransformedValueIfErr() {
            assertEquals(
                expected = 2000,
                actual = Err(4000).recover { 2000 }.get()
            )
        }
    }
}
