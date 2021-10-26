package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    class OrElseThrow {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(5000),
                actual = Ok(5000).orElseThrow()
            )
        }

        @Test
        fun returnsTransformedValueIfErr() {
            val error = RuntimeException("or else throw")

            fun provideError(): Result<String, Throwable> {
                return Err(error)
            }

            assertFailsWith<RuntimeException>(error.message, provideError()::orElseThrow)
        }
    }

    class Recover {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).recover { 4000 }
            )
        }

        @Test
        fun returnsTransformedValueIfErr() {
            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).recover { 2000 }
            )
        }
    }

    class RecoverIf {
        @Test
        fun returnsValueIfOk() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).recoverIf(::predicate) { 2000 }
            )
        }

        @Test
        fun returnsTransformedErrorAsOkIfErrAndPredicateMatch() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).recoverIf(::predicate) { 2000 }
            )
        }

        @Test
        fun doesNotReturnTransformedErrorAsOkIfErrAndPredicateDoesNotMatch() {
            fun predicate(int: Int): Boolean {
                return int == 3000
            }

            assertEquals(
                expected = Err(4000),
                actual = Err(4000).recoverIf(::predicate) { 2000 }
            )
        }

        @Test
        fun returnErrIfErrAndPredicateDoesNotMatch() {
            fun predicate(int: Int): Boolean {
                return int == 3000
            }

            assertEquals(
                expected = Err(4000),
                actual = Err(4000).recoverIf(::predicate) { 2000 }
            )
        }
    }

    class RecoverUnless {
        @Test
        fun returnsValueIfOk() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).recoverUnless(::predicate) { 2000 }
            )
        }

        @Test
        fun returnsTransformedErrorAsOkIfErrAndPredicateDoesNotMatch() {
            fun predicate(int: Int): Boolean {
                return int == 3000
            }

            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).recoverUnless(::predicate) { 2000 }
            )
        }

        @Test
        fun doesNotReturnTransformedErrorAsOkIfErrAndPredicateMatches() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Err(4000),
                actual = Err(4000).recoverUnless(::predicate) { 2000 }
            )
        }

        @Test
        fun returnErrIfErrAndPredicateDoesMatch() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Err(4000),
                actual = Err(4000).recoverUnless(::predicate) { 2000 }
            )
        }
    }
}
