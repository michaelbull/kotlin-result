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
                actual = Ok(230).and { Ok(500) }.get()
            )
        }

        @Test
        fun returnsValueIfErr() {
            assertEquals(
                expected = "hello world",
                actual = Ok(300).and { Err("hello world") }.getError()
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

    class AndThenRun {
        @Test
        fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = 12,
                actual = Ok(5).andThenRun { Ok(this + 7) }.get()
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertSame(
                expected = AndError,
                actual = Ok(20).andThenRun { Ok(this + 43) }.andThenRun { Err(AndError) }.getError()!!
            )
        }

        @Test
        fun canBeCalledWithMethodCall() {
            assertEquals(
                expected = 3,
                actual = Ok("abc").andThenRun { Ok(length) }.get()
            )
        }
    }

    class AndThenMap {
        @Test
        fun returnsTransformedValueIfOk() {
            assertEquals(
                expected = 12,
                actual = Ok(5).andThenMap { this + 7 }.get()
            )
        }

        @Test
        fun returnsErrorIfErr() {
            val functionWithError : (Int) -> Result <String, Any> =  { Err(AndError) }
            assertSame(
                expected = AndError,
                actual = Ok(20).andThen(functionWithError).andThenMap { this + 43 }.getError()!!
            )
        }

        @Test
        fun canBeCalledWithMethodCall() {
            assertEquals(
                expected = 3,
                actual = Ok("abc").andThenMap { length }.get()
            )
        }
    }
}
