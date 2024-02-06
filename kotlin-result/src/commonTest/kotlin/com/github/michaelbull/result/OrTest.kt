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

    class ThrowIf {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(200),
                actual = Ok(200).throwIf { true }
            )
        }

        @Test
        fun returnsErrIfPredicateDoesNotMatch() {
            val error = RuntimeException("throw if")

            assertEquals(
                expected = Err(error),
                actual = Err(error).throwIf { false }
            )
        }

        @Test
        fun throwsErrIfPredicateMatches() {
            val error = RuntimeException("throw if")

            assertFailsWith<RuntimeException>(error.message) {
                Err(error).throwIf { true }
            }
        }
    }

    class ThrowUnless {
        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(500),
                actual = Ok(500).throwUnless { false }
            )
        }

        @Test
        fun returnsErrIfPredicateMatches() {
            val error = RuntimeException("example")

            assertEquals(
                expected = Err(error),
                actual = Err(error).throwUnless { true }
            )
        }

        @Test
        fun throwsErrIfPredicateDoesNotMatch() {
            val error = RuntimeException("throw unless")

            assertFailsWith<RuntimeException>(error.message) {
                Err(error).throwUnless { false }
            }
        }
    }
}
