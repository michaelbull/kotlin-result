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

    class AndThenRecover {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 5,
                actual = Ok(5).andThenRecover { Ok(7) }.get()
            )
        }

        @Test
        fun returnsTransformValueIfErr() {
            assertEquals(
                expected = 20,
                actual = Err(AndError).andThenRecover { Ok(20) }.get()
            )
        }
    }

    class AndThenRecoverIf {
        @Test
        fun returnsValueIfOk() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).andThenRecoverIf(::predicate) { Ok(2000) }
            )
        }

        @Test
        fun returnsTransformedErrorAsOkIfErrAndPredicateMatch() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).andThenRecoverIf(::predicate) { Ok(2000) }
            )
        }

        @Test
        fun returnsTransformedErrorAsErrorIfErrAndPredicateMatch() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Err(2000),
                actual = Err(4000).andThenRecoverIf(::predicate) { Err(2000) }
            )
        }

        @Test
        fun doesNotReturnTransformationResultIfErrAndPredicateDoesNotMatch() {
            fun predicate(int: Int): Boolean {
                return int == 3000
            }

            assertEquals(
                expected = Err(4000),
                actual = Err(4000).andThenRecoverIf(::predicate) { Ok(2000) }
            )
        }
    }

    class AndThenRecoverUnless {
        @Test
        fun returnsValueIfOk() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).andThenRecoverUnless(::predicate) { Ok(2000) }
            )
        }

        @Test
        fun returnsTransformedErrorAsOkIfErrAndPredicateDoesNotMatch() {
            fun predicate(int: Int): Boolean {
                return int == 3000
            }

            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).andThenRecoverUnless(::predicate) { Ok(2000) }
            )
        }

        @Test
        fun returnsTransformedErrorAsErrorIfErrAndPredicateDoesNotMatch() {
            fun predicate(int: Int): Boolean {
                return int == 3000
            }

            assertEquals(
                expected = Err(2000),
                actual = Err(4000).andThenRecoverUnless(::predicate) { Err(2000) }
            )
        }

        @Test
        fun doesNotReturnTransformationResultIfErrAndPredicateMatches() {
            fun predicate(int: Int): Boolean {
                return int == 4000
            }

            assertEquals(
                expected = Err(4000),
                actual = Err(4000).andThenRecoverUnless(::predicate) { Ok(2000) }
            )
        }
    }
}
