package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class UnwrapTest {
    internal class `unwrap` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = 5000,
                actual = Ok(5000).unwrap()
            )
        }

        @Test
        internal fun throwsExceptionIfErr() {
            assertFailsWith<UnwrapException>("called Result.wrap on an Err value 5000") {
                Err(5000).unwrap()
            }
        }
    }

    internal class `expect` {
        @Test
        internal fun returnsValueIfOk() {
            assertEquals(
                expected = 1994,
                actual = Ok(1994).expect { "the year should be" }
            )
        }

        @Test
        internal fun throwsExceptionIfErr() {
            val message = object {
                override fun toString() = "the year should be"
            }

            assertFailsWith<UnwrapException>("the year should be 1994") {
                Err(1994).expect { message }
            }
        }
    }

    internal class `unwrapError` {
        @Test
        internal fun throwsExceptionIfOk() {
            assertFailsWith<UnwrapException>("called Result.unwrapError on an Ok value example") {
                Ok("example").unwrapError()
            }
        }

        @Test
        internal fun returnsErrorIfErr() {
            assertEquals(
                expected = "example",
                actual = Err("example").unwrapError()
            )
        }
    }

    internal class `expectError` {
        @Test
        internal fun throwsExceptionIfOk() {
            val message = object {
                override fun toString() = "the year should be"
            }

            assertFailsWith<UnwrapException>("the year should be 2010") {
                Ok(2010).expectError { message }
            }
        }

        @Test
        internal fun returnsErrorIfErr() {
            assertEquals(
                expected = 2010,
                actual = Err(2010).expectError { "the year should be" }
            )
        }
    }
}
