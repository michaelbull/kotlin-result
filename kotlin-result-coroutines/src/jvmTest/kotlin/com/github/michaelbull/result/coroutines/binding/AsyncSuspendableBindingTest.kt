package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class AsyncSuspendableBindingTest {

    private sealed class BindingError {
        object BindingErrorA : BindingError()
        object BindingErrorB : BindingError()
    }

    @Test
    fun returnsOkIfAllBindsSuccessful() {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(100)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(100)
            return Ok(2)
        }

        runBlockingTest {
            val result = binding<Int, BindingError> {
                val x = async { provideX().bind() }
                val y = async { provideY().bind() }
                x.await() + y.await()
            }

            assertTrue(result is Ok)
            assertEquals(
                expected = 3,
                actual = result.value
            )
        }
    }

    @Test
    fun returnsFirstErrIfBindingFailed() {
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

        runBlockingTest {
            val result = binding<Int, BindingError> {
                val x = async { provideX().bind() }
                val y = async { provideY().bind() }
                val z = async { provideZ().bind() }
                x.await() + y.await() + z.await()
            }

            assertTrue(result is Err)
            assertEquals(
                expected = BindingError.BindingErrorB,
                actual = result.error
            )
        }
    }

    @Test
    fun returnsStateChangedForOnlyTheFirstAsyncBindFailWhenEagerlyCancellingBinding() {
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

        val dispatcherA = TestCoroutineDispatcher()
        val dispatcherB = TestCoroutineDispatcher()

        runBlocking {
            val result = binding<Int, BindingError> {
                val x = async(dispatcherA) { provideX().bind() }
                val y = async(dispatcherB) { provideY().bind() }
                dispatcherA.advanceTimeBy(2)
                x.await() + y.await()
            }

            assertTrue(result is Err)
            assertEquals(
                expected = BindingError.BindingErrorA,
                actual = result.error
            )
            assertTrue(xStateChange)
            assertFalse(yStateChange)
        }
    }

    @Test
    fun returnsStateChangedForOnlyTheFirstLaunchBindFailWhenEagerlyCancellingBinding() {
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

        val dispatcherA = TestCoroutineDispatcher()
        val dispatcherB = TestCoroutineDispatcher()
        val dispatcherC = TestCoroutineDispatcher()

        runBlocking {
            val result = binding<Unit, BindingError> {
                launch(dispatcherA) { provideX().bind() }
                dispatcherA.advanceTimeBy(20)
                launch(dispatcherB) { provideY().bind() }
                dispatcherB.advanceTimeBy(20)
                launch(dispatcherC) { provideZ().bind() }
            }

            assertTrue(result is Err)
            assertEquals(
                expected = BindingError.BindingErrorA,
                actual = result.error
            )
            assertTrue(xStateChange)
            assertTrue(yStateChange)
            assertFalse(zStateChange)
        }
    }
}
