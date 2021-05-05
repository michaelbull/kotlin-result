package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

        runBlocking {
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

        runBlocking {
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
            // as this test uses a new thread for each coroutine, we want to set this delay to a high enough number that
            // there isn't any chance of a jvm run actually completing this suspending function in this thread first
            // otherwise the assertions might fail.
            delay(100)
            yStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        runBlocking {
            val result = binding<Int, BindingError> {
                val x = async(newThread("ThreadA")) { provideX().bind() }
                val y = async(newThread("ThreadB")) { provideY().bind() }
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

        runBlocking {
            val result = binding<Unit, BindingError> {
                launch(newThread("Thread A")) { provideX().bind() }
                launch(newThread("Thread B")) { provideY().bind() }
                launch(newThread("Thread C")) { provideZ().bind() }
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

    private fun newThread(name: String): CoroutineContext {
        return Executors.newSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName(name)
    }
}
