package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class AsyncCoroutineBindingTest {

    private sealed interface BindingError {
        data object BindingErrorA : BindingError
        data object BindingErrorB : BindingError
    }

    @Test
    fun returnsOkIfAllBindsSuccessful() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(100.milliseconds)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(100.milliseconds)
            return Ok(2)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async { provideX() }
            val y = async { provideY() }
            x.await() + y.await()
        }

        assertEquals(
            expected = Ok(3),
            actual = result
        )
    }

    @Test
    fun returnsFirstErrIfBindingFailed() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(3.milliseconds)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(3.milliseconds)
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(2.milliseconds)
            return Err(BindingError.BindingErrorB)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async { provideX() }
            val y = async { provideY() }
            val z = async { provideZ() }
            x.await() + y.await() + z.await()
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorB),
            actual = result
        )
    }

    @Test
    fun returnsStateChangedForOnlyTheFirstAsyncBindFailWhenEagerlyCancellingBinding() = runTest {
        var xStateChange = false
        var yStateChange = false

        suspend fun provideX(): Result<Int, BindingError> {
            delay(2.milliseconds)
            xStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorB> {
            delay(3.milliseconds)
            yStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        val dispatcherA = StandardTestDispatcher(testScheduler)
        val dispatcherB = StandardTestDispatcher(testScheduler)

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async(dispatcherA) { provideX() }
            val y = async(dispatcherB) { provideY() }

            testScheduler.advanceTimeBy(2.milliseconds)
            testScheduler.runCurrent()

            x.await() + y.await()
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorA),
            actual = result
        )

        assertTrue(xStateChange)
        assertFalse(yStateChange)
    }

    @Test
    fun returnsStateChangedForOnlyTheFirstLaunchBindFailWhenEagerlyCancellingBinding() = runTest {
        var xStateChange = false
        var yStateChange = false
        var zStateChange = false

        suspend fun provideX(): Result<Int, BindingError> {
            delay(1.milliseconds)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20.milliseconds)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(100.milliseconds)
            zStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        val dispatcherA = StandardTestDispatcher(testScheduler)
        val dispatcherB = StandardTestDispatcher(testScheduler)
        val dispatcherC = StandardTestDispatcher(testScheduler)

        val result: Result<Unit, BindingError> = coroutineBinding {
            launch(dispatcherA) { provideX().bind() }

            testScheduler.advanceTimeBy(20.milliseconds)
            testScheduler.runCurrent()

            launch(dispatcherB) { provideY().bind() }

            testScheduler.advanceTimeBy(20.milliseconds)
            testScheduler.runCurrent()

            launch(dispatcherC) { provideZ().bind() }
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorA),
            actual = result
        )

        assertTrue(xStateChange)
        assertTrue(yStateChange)
        assertFalse(zStateChange)
    }

    @Test
    fun shouldHandleNestedBindings() = runTest {
        var xStateChange = false
        var yStateChange = false
        var zStateChange = false

        suspend fun provideX(): Result<Int, BindingError> {
            delay(1.milliseconds)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideXWrapped() = coroutineBinding {
            provideX().bind()
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20.milliseconds)
            yStateChange = true
            return Ok(1)
        }

        suspend fun provideYWrapped() = coroutineBinding {
            provideY().bind()
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(100.milliseconds)
            zStateChange = true
            return Ok(1)
        }

        suspend fun provideZWrapped() = coroutineBinding {
            provideZ().bind()
        }

        val dispatcherA = StandardTestDispatcher(testScheduler)
        val dispatcherB = StandardTestDispatcher(testScheduler)
        val dispatcherC = StandardTestDispatcher(testScheduler)

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async(dispatcherA) { provideXWrapped() }
            val y = async(dispatcherB) { provideYWrapped() }

            testScheduler.advanceTimeBy(2.milliseconds)
            testScheduler.runCurrent()

            val z = async(dispatcherC) { provideZWrapped() }

            x.await() + y.await() + z.await()
        }

        assertEquals(
            expected = Ok(3),
            actual = result
        )

        assertTrue(xStateChange)
        assertTrue(yStateChange)
        assertTrue(zStateChange)
    }

    @Test
    fun shouldHandleExceptionsWithNestedBindings() = runTest {
        var xStateChange = false
        var yStateChange = false
        var zStateChange = false

        suspend fun provideX(): Result<Int, BindingError> {
            delay(1.milliseconds)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideXWrapped() = coroutineBinding {
            provideX().bind()
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20.milliseconds)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideYWrapped() = coroutineBinding {
            provideY().bind()
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(100.milliseconds)
            zStateChange = true
            return Ok(1)
        }

        suspend fun provideZWrapped() = coroutineBinding {
            provideZ().bind()
        }

        val dispatcherA = StandardTestDispatcher(testScheduler)
        val dispatcherB = StandardTestDispatcher(testScheduler)
        val dispatcherC = StandardTestDispatcher(testScheduler)

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async(dispatcherA) { provideXWrapped() }
            val y = async(dispatcherB) { provideYWrapped() }

            testScheduler.advanceTimeBy(2.milliseconds)
            testScheduler.runCurrent()

            val z = async(dispatcherC) { provideZWrapped() }

            x.await() + y.await() + z.await()
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorA),
            actual = result
        )

        assertTrue(xStateChange)
        assertTrue(yStateChange)
        assertFalse(zStateChange)
    }
}
