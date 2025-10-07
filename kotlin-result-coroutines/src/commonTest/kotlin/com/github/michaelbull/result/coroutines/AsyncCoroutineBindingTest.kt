package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AsyncCoroutineBindingTest {

    private sealed interface BindingError {
        data object BindingErrorA : BindingError
        data object BindingErrorB : BindingError
    }

    @Test
    fun returnsOkIfAllBindsSuccessful() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(100)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(100)
            return Ok(2)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async { provideX().bind() }
            val y = async { provideY().bind() }
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
            delay(3)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(3)
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(2)
            return Err(BindingError.BindingErrorB)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async { provideX().bind() }
            val y = async { provideY().bind() }
            val z = async { provideZ().bind() }
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
            delay(2)
            xStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorB> {
            delay(3)
            yStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        val dispatcherA = StandardTestDispatcher(testScheduler)
        val dispatcherB = StandardTestDispatcher(testScheduler)

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = async(dispatcherA) { provideX().bind() }
            val y = async(dispatcherB) { provideY().bind() }

            testScheduler.advanceTimeBy(2)
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
            delay(1)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(100)
            zStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        val dispatcherA = StandardTestDispatcher(testScheduler)
        val dispatcherB = StandardTestDispatcher(testScheduler)
        val dispatcherC = StandardTestDispatcher(testScheduler)

        val result: Result<Unit, BindingError> = coroutineBinding {
            launch(dispatcherA) { provideX().bind() }

            testScheduler.advanceTimeBy(20)
            testScheduler.runCurrent()

            launch(dispatcherB) { provideY().bind() }

            testScheduler.advanceTimeBy(20)
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
            delay(1)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideXWrapped() = coroutineBinding {
            provideX().bind()
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20)
            yStateChange = true
            return Ok(1)
        }

        suspend fun provideYWrapped() = coroutineBinding {
            provideY().bind()
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(100)
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
            val x = async(dispatcherA) { provideXWrapped().bind() }
            val y = async(dispatcherB) { provideYWrapped().bind() }

            testScheduler.advanceTimeBy(2)
            testScheduler.runCurrent()

            val z = async(dispatcherC) { provideZWrapped().bind() }

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
            delay(1)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideXWrapped() = coroutineBinding {
            provideX().bind()
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideYWrapped() = coroutineBinding {
            provideY().bind()
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(100)
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
            val x = async(dispatcherA) { provideXWrapped().bind() }
            val y = async(dispatcherB) { provideYWrapped().bind() }

            testScheduler.advanceTimeBy(2)
            testScheduler.runCurrent()

            val z = async(dispatcherC) { provideZWrapped().bind() }

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
