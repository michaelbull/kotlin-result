package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

@ExperimentalCoroutinesApi
class RunSuspendCatchingTest {

    @Test
    fun propagatesCoroutineCancellation() {
        val testDispatcher = TestCoroutineDispatcher()
        val testScope = TestCoroutineScope(testDispatcher)

        testScope.runBlockingTest {
            var value: String? = null

            launch { // Outer scope
                launch { // Inner scope
                    val result = runSuspendCatching {
                        delay(4_000)
                        "value"
                    }

                    // The coroutine should be cancelled before reaching here
                    result.onSuccess { value = it }
                }
                testDispatcher.advanceTimeBy(2_000)

                // Cancel outer scope, which should cancel inner scope
                cancel()
            }
            assertNull(value)
        }
    }

    @Test
    fun returnsOkIfInvocationSuccessful() {
        val testDispatcher = TestCoroutineDispatcher()
        val testScope = TestCoroutineScope(testDispatcher)

        testScope.runBlockingTest {
            val callback = { "example" }
            val result = runSuspendCatching(callback)

            assertEquals(
                expected = "example",
                actual = result.get()
            )
        }
    }

    @Test
    fun returnsErrIfInvocationFailsWithAnythingOtherThanCancellationException() {
        val testDispatcher = TestCoroutineDispatcher()
        val testScope = TestCoroutineScope(testDispatcher)

        testScope.runBlockingTest {
            val exception = IllegalArgumentException("throw me")
            val callback = { throw exception }
            val result = runSuspendCatching(callback)

            assertSame(
                expected = exception,
                actual = result.getError()
            )
        }
    }
}
