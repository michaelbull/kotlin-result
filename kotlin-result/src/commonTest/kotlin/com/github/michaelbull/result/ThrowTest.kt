package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ThrowTest {

    class ThrowIfTest {
        @Test
        fun throwsIfPredicateReturnsTrue() {
            val result: Result<Any, Throwable> = Err(RuntimeException())

            assertFailsWith<RuntimeException> {
                result.throwIf { _ -> true }
            }
        }

        @Test
        fun nothingThrownIfPredicateReturnsFalse() {
            val result: Result<Any, Exception> = Err(NullPointerException())

            result.throwIf { _ -> false }
        }

        @Test
        fun doesntAlterOkValue() {
            val result: Result<String, Throwable> = Ok("ok")

            assertEquals(
                expected = "ok",
                actual = result.throwIf(predicate = { false }).unwrap()
            )
        }
    }

    class ThrowUnlessTest {
        @Test
        fun throwsIfPredicateReturnsFalse() {
            val result: Result<Any, Throwable> = Err(RuntimeException())

            assertFailsWith<RuntimeException> {
                result.throwUnless { _ -> false }
            }
        }

        @Test
        fun nothingThrownIfPredicateReturnsTrue() {
            val result: Result<Any, Exception> = Err(NullPointerException())

            result.throwUnless { _ -> true }
        }

        @Test
        fun doesntAlterOkValue() {
            val result: Result<String, Throwable> = Ok("ok")

            assertEquals(
                expected = "ok",
                actual = result.throwUnless(predicate = { false }).unwrap()
            )
        }
    }
}
