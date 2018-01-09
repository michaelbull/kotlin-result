package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

internal class OrTest {
    private object OrError

    internal class `or` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = 500,
                actual = Ok(500).or { Ok(1000) }.get()
            )
        }

        @Test
        internal fun returnsDefaultValueIfErr() {
            assertEquals(
                expected = 5000,
                actual = Err(OrError).or { Ok(5000) }.get()
            )
        }
    }

    internal class `orElse` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = 3000,
                actual = Ok(3000).orElse { Ok(4000) }.get()
            )
        }

        @Test
        internal fun returnsTransformedValueIfErr() {
            assertEquals(
                expected = 2000,
                actual = Err(4000).orElse { Ok(2000) }.get()
            )
        }
    }
}
