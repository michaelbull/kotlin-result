package com.github.michaelbull.result

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class ResultIteratorTest {
    @Test
    internal fun `hasNext should return true if unyielded and result is ok`() {
        val iterator = ok("hello").iterator()
        assertThat(iterator.hasNext(), equalTo(true))
    }

    @Test
    internal fun `hasNext should return false if result is not ok`() {
        val iterator = err("hello").iterator()
        assertThat(iterator.hasNext(), equalTo(false))
    }

    @Test
    internal fun `hasNext should return false if yielded`() {
        val iterator = ok("hello").iterator()
        iterator.next()
        assertThat(iterator.hasNext(), equalTo(false))
    }

    @Test
    internal fun `next should return the result value if unyielded and result is ok`() {
        val value = ok("hello").iterator().next()
        assertThat(value, equalTo("hello"))
    }

    @Test
    internal fun `next should throw NoSuchElementException if unyielded and result is not ok`() {
        val iterator = err("hello").iterator()

        assertThrows(NoSuchElementException::class.java) {
            iterator.next()
        }
    }

    @Test
    internal fun `next should throw NoSuchElementException if yielded and result is ok`() {
        val iterator = ok("hello").iterator()
        iterator.next()

        assertThrows(NoSuchElementException::class.java) {
            iterator.next()
        }
    }

    @Test
    internal fun `remove should make hasNext return false`() {
        val iterator = ok("hello").mutableIterator()
        iterator.remove()
        assertThat(iterator.hasNext(), equalTo(false))
    }

    @Test
    internal fun `remove should make next throw NoSuchElementException`() {
        val iterator = ok("hello").mutableIterator()
        iterator.remove()

        assertThrows(NoSuchElementException::class.java) {
            iterator.next()
        }
    }
}
