package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResultIteratorTest {
    class HasNext {
        @Test
        fun returnsTrueIfUnyieldedAndOk() {
            val iterator = Ok("hello").iterator()
            assertTrue { iterator.hasNext() }
        }

        @Test
        fun returnsFalseIfErr() {
            val iterator = Err("hello").iterator()
            assertFalse { iterator.hasNext() }
        }

        @Test
        fun returnsFalseIfYielded() {
            val iterator = Ok("hello").iterator()
            iterator.next()
            assertFalse { iterator.hasNext() }
        }
    }

    class Next {
        @Test
        fun returnsValueIfUnyieldedAndOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").iterator().next()
            )
        }

        @Test
        fun throwsExceptionIfUnyieldedAndErr() {
            val iterator = Err("hello").iterator()

            assertFailsWith<NoSuchElementException> {
                @Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
                iterator.next()
            }
        }

        @Test
        fun throwsExceptionIfYieldedAndOk() {
            val iterator = Ok("hello").iterator()
            iterator.next()
            assertFailsWith<NoSuchElementException> { iterator.next() }
        }
    }

    class Remove {
        @Test
        fun makesHasNextReturnFalse() {
            val iterator = Ok("hello").mutableIterator()
            iterator.remove()
            assertFalse { iterator.hasNext() }
        }

        @Test
        fun makesNextThrowException() {
            val iterator = Ok("hello").mutableIterator()
            iterator.remove()
            assertFailsWith<NoSuchElementException> { iterator.next() }
        }
    }
}
