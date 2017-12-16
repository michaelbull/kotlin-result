package com.github.michaelbull.result

import kotlin.test.*

internal class ResultIteratorTest {
    internal class `hasNext` {
        @Test
        internal fun returnsTrueIfUnyieldedAndOk() {
            val iterator = Ok("hello").iterator()
            assertTrue { iterator.hasNext() }
        }

        @Test
        internal fun returnsFalseIfErr() {
            val iterator = Err("hello").iterator()
            assertFalse { iterator.hasNext() }
        }

        @Test
        internal fun returnsFalseIfYielded() {
            val iterator = Ok("hello").iterator()
            iterator.next()
            assertFalse { iterator.hasNext() }
        }
    }

    internal class `next` {
        @Test
        internal fun returnsValueIfUnyieldedAndOk() {
            assertEquals(
                expected = "hello",
                actual = Ok("hello").iterator().next()
            )
        }

        @Test
        internal fun throwsExceptionIfUnyieldedAndErr() {
            val iterator = Err("hello").iterator()
            assertFailsWith<NoSuchElementException> { iterator.next() }
        }

        @Test
        internal fun throwsExceptionIfYieldedAndOk() {
            val iterator = Ok("hello").iterator()
            iterator.next()
            assertFailsWith<NoSuchElementException> { iterator.next() }
        }
    }

    internal class `remove` {
        @Test
        internal fun makesHasNextReturnFalse() {
            val iterator = Ok("hello").mutableIterator()
            iterator.remove()
            assertFalse { iterator.hasNext() }
        }

        @Test
        internal fun makesNextThrowException() {
            val iterator = Ok("hello").mutableIterator()
            iterator.remove()
            assertFailsWith<NoSuchElementException> { iterator.next() }
        }
    }
}
