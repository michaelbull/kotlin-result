package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class RunSuspendCatchingTest {

    @Test
    fun propagatesCoroutineCancellation() = runBlockingTest {
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

            advanceTimeBy(2_000)

            // Cancel outer scope, which should cancel inner scope
            cancel()
        }

        assertNull(value)
    }

    @Test
    fun returnsOkIfInvocationSuccessful() = runBlockingTest {
        val block = { "example" }
        val result = runSuspendCatching(block)

        assertEquals(
            expected = Ok("example"),
            actual = result
        )
    }

    @Test
    fun returnsErrIfInvocationFailsWithAnythingOtherThanCancellationException() = runBlockingTest {
        val exception = IllegalArgumentException("throw me")
        val block = { throw exception }
        val result = runSuspendCatching(block)

        assertEquals(
            expected = Err(exception),
            actual = result
        )
    }
}
