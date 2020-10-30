package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
            delay(2)
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(1)
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
    fun returnsAllStateChangedIfAnyBindFailedWhenBindingSetToNotCancelEagerly() {
        var xStateChange = false
        var yStateChange = false
        var zStateChange = false
        suspend fun provideX(): Result<Int, BindingError> {
            delay(3)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(2)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(1)
            zStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        runBlocking {
            val result = binding<Int, BindingError>(eagerlyCancel = false) {
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
            assertTrue(xStateChange)
            assertTrue(yStateChange)
            assertTrue(zStateChange)
        }
    }

    @Test
    fun returnsStateChangedForOnlyTheFirstBindFailedWhenBindingSetToCancelEagerly() {
        var xStateChange = false
        var yStateChange = false
        var zStateChange = false
        suspend fun provideX(): Result<Int, BindingError> {
            delay(3)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(2)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(1)
            zStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        runBlocking {
            val result = binding<Int, BindingError>(eagerlyCancel = true) {
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
            assertFalse(xStateChange)
            assertFalse(yStateChange)
            assertTrue(zStateChange)
        }
    }
}
