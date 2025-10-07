package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoroutineBindingTest {

    private sealed interface BindingError {
        data object BindingErrorA : BindingError
        data object BindingErrorB : BindingError
        data object BindingErrorC : BindingError
    }

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

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        assertEquals(
            expected = Ok(3),
            actual = result,
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

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = provideX().bind()
            val y = provideY(x.toInt()).bind()
            y
        }

        assertEquals(
            expected = Ok(3),
            actual = result,
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
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError> {
            delay(1)
            return Ok(2)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorA),
            actual = result,
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
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError> {
            delay(1)
            zStateChange = true
            return Err(BindingError.BindingErrorA)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorA),
            actual = result,
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
            return Err(BindingError.BindingErrorA)
        }

        suspend fun provideZ(): Result<Int, BindingError> {
            delay(1)
            return Ok(2)
        }

        val result: Result<Int, BindingError> = coroutineBinding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y.toInt() + z
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorA),
            actual = result,
        )
    }

    @Test
    fun shouldHandleExceptionsWithMultipleNestedBindings() = runTest {
        val result: Result<Int, BindingError> = coroutineBinding {
            val b: Result<Int, BindingError> = coroutineBinding {
                val c: Result<Int, BindingError> = coroutineBinding {
                    Err(BindingError.BindingErrorC).bind()
                }

                assertEquals(Err(BindingError.BindingErrorC), c)

                Ok(2).bind()
            }

            assertEquals(Ok(2), b)

            Err(BindingError.BindingErrorB).bind()
        }

        assertEquals(
            expected = Err(BindingError.BindingErrorB),
            actual = result
        )
    }
}
