package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
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
    fun returnsOkIfAllBindsSuccessful() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(100)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(100)
            return Ok(2)
        }

        val result = binding<Int, BindingError> {
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
            delay(10)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(20)
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(1)
            return Err(BindingError.BindingErrorB)
        }

        val result = binding<Int, BindingError> {
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
        var zStateChange = false

        suspend fun provideX(): Result<Int, BindingError> {
            delay(20)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError.BindingErrorA> {
            delay(10)
            yStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError.BindingErrorB> {
            delay(1)
            zStateChange = true
            return Err(BindingError.BindingErrorB)
        }

        val result = binding<Int, BindingError> {
            val x = async { provideX().bind() }
            val y = async { provideY().bind() }
            val z = async { provideZ().bind() }

            x.await() + y.await() + z.await()
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorB),
            actual = result
        )

        assertFalse(xStateChange)
        assertFalse(yStateChange)
        assertTrue(zStateChange)
    }
}
