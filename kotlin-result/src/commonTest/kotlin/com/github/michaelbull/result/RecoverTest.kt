package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class RecoverTest {
    private object RecoverError

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

    class RecoverCatching {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).recoverCatching { 4000 }
            )
        }

        @Test
        fun returnsTransformedValueIfErr() {
            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).recoverCatching { 2000 }
            )
        }

        @Test
        fun returnsErrorIfTransformerThrows() {
            val exception = IllegalArgumentException("throw me")

            assertEquals(
                expected = exception,
                actual = Err(4000)
                    .recoverCatching { throw exception }
                    .getError()
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
                actual = Err(RecoverError).andThenRecover { Ok(20) }.get()
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
