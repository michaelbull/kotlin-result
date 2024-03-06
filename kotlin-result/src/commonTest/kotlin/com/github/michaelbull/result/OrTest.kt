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
                expected = Ok(500),
                actual = Ok(500).or(Ok(1000))
            )
        }

        @Test
        fun returnsDefaultValueIfErr() {
            assertEquals(
                expected = Ok(5000),
                actual = Err(OrError).or(Ok(5000))
            )
        }
    }

    class OrElse {

        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok(3000),
                actual = Ok(3000).orElse { Ok(4000) }
            )
        }

        @Test
        fun returnsTransformedValueIfErr() {
            assertEquals(
                expected = Ok(2000),
                actual = Err(4000).orElse { Ok(2000) }
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
        fun throwsErrorIfErr() {
            val result = Err(RuntimeException("or else throw"))
            assertFailsWith<RuntimeException>(block = result::orElseThrow)
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
            val result: Result<Int, Exception> = Err(RuntimeException("throw if"))

            assertEquals(
                expected = result,
                actual = result.throwIf { false }
            )
        }

        @Test
        fun throwsErrIfPredicateMatches() {
            val result = Err(RuntimeException("throw if"))

            assertFailsWith<RuntimeException> {
                result.throwIf { true }
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
            val result: Result<Int, Exception> = Err(RuntimeException("throw unless"))

            assertEquals(
                expected = result,
                actual = result.throwUnless { true }
            )
        }

        @Test
        fun throwsErrIfPredicateDoesNotMatch() {
            val result = Err(RuntimeException("throw unless"))

            assertFailsWith<RuntimeException> {
                result.throwUnless { false }
            }
        }
    }
}
