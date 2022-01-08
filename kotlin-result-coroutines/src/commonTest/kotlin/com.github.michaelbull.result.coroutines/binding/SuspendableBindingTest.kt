package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SuspendableBindingTest {

    private object BindingError

    @Test
    fun returnsOkIfAllBindsSuccessful() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(1)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(1)
            return Ok(2)
        }

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        assertEquals(
            expected = Ok(3),
            actual = result
        )
    }

    @Test
    fun returnsOkIfAllBindsOfDifferentTypeAreSuccessful() = runTest {
        suspend fun provideX(): Result<String, BindingError> {
            delay(1)
            return Ok("1")
        }

        suspend fun provideY(x: Int): Result<Int, BindingError> {
            delay(1)
            return Ok(x + 2)
        }

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY(x.toInt()).bind()
            y
        }

        assertEquals(
            expected = Ok(3),
            actual = result
        )
    }

    @Test
    fun returnsFirstErrIfBindingFailed() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(1)
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(1)
            return Err(BindingError)
        }

        suspend fun provideZ(): Result<Int, BindingError> {
            delay(1)
            return Ok(2)
        }

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result
        )
    }

    @Test
    fun returnsStateChangedUntilFirstBindFailed() = runTest {
        var xStateChange = false
        var yStateChange = false
        var zStateChange = false

        suspend fun provideX(): Result<Int, BindingError> {
            delay(1)
            xStateChange = true
            return Ok(1)
        }

        suspend fun provideY(): Result<Int, BindingError> {
            delay(10)
            yStateChange = true
            return Err(BindingError)
        }

        suspend fun provideZ(): Result<Int, BindingError> {
            delay(1)
            zStateChange = true
            return Err(BindingError)
        }

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result
        )

        assertTrue(xStateChange)
        assertTrue(yStateChange)
        assertFalse(zStateChange)
    }

    @Test
    fun returnsFirstErrIfBindingsOfDifferentTypesFailed() = runTest {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(1)
            return Ok(1)
        }

        suspend fun provideY(): Result<String, BindingError> {
            delay(1)
            return Err(BindingError)
        }

        suspend fun provideZ(): Result<Int, BindingError> {
            delay(1)
            return Ok(2)
        }

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y.toInt() + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result
        )
    }
}
