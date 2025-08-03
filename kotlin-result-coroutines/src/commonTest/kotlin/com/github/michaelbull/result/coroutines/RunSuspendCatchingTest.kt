package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class RunSuspendCatchingTest {

    @Test
    fun propagatesCoroutineCancellation() = runTest(UnconfinedTestDispatcher()) {
        var value: String? = null

        launch(CoroutineName("outer scope")) {
            launch(CoroutineName("inner scope")) {
                val result = runSuspendCatching {
                    delay(4_000)
                    "value"
                }

                // The coroutine should be cancelled before reaching here
                result.onSuccess { value = it }
            }

            testScheduler.advanceTimeBy(2_000)
            testScheduler.runCurrent()

            // Cancel outer scope, which should cancel inner scope
            cancel()
        }

        assertNull(value)
    }

    @Test
    fun returnsOkIfInvocationSuccessful() = runTest {
        val block = { "example" }
        val result = runSuspendCatching(block)

        assertEquals(
            expected = Ok("example"),
            actual = result,
        )
    }

    @Test
    fun returnsErrIfInvocationFailsWithAnythingOtherThanCancellationException() = runTest {
        val exception = IllegalArgumentException("throw me")
        val block = { throw exception }
        val result = runSuspendCatching(block)

        assertEquals(
            expected = Err(exception),
            actual = result,
        )
    }
}
