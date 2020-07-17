package com.github.michaelbull.result

import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.github.michaelbull.result.coroutines.binding

class SuspendableBindingTest {

    object BindingError

    @Test
    fun returnsOkIfAllBindsSuccessful() {
        suspend fun provideX(): Result<Int, BindingError> {
            delay(1)
            return Ok(1)
        }
        suspend fun provideY(): Result<Int, BindingError> {
            delay(1)
            return Ok(2)
        }

        runTest {
            val result = binding<Int, BindingError> {
                val x = provideX().bind()
                val y = provideY().bind()
                x + y
            }
            assertTrue(result is Ok)
            assertEquals(3, result.value)
        }
    }

    @Test
    fun returnsOkIfAllBindsOfDifferentTypeAreSuccessful() {
        suspend fun provideX(): Result<String, BindingError> {
            delay(1)
            return Ok("1")
        }
        suspend fun provideY(x: Int): Result<Int, BindingError> {
            delay(1)
            return Ok(x + 2)
        }

        runTest {
            val result = binding<Int, BindingError> {
                val x = provideX().bind()
                val y = provideY(x.toInt()).bind()
                y
            }

            assertTrue(result is Ok)
            assertEquals(3, result.value)
        }
    }

    @Test
    fun returnsFirstErrIfBindingFailed() {
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

        runTest {
            val result = binding<Int, BindingError> {
                val x = provideX().bind()
                val y = provideY().bind()
                val z = provideZ().bind()
                x + y + z
            }

            assertTrue(result is Err)
            assertEquals(BindingError, result.error)
        }
    }

    @Test
    fun returnsFirstErrIfBindingsOfDifferentTypesFailed() {
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

        runTest {
            val result = binding<Int, BindingError> {
                val x = provideX().bind()
                val y = provideY().bind()
                val z = provideZ().bind()
                x + y.toInt() + z
            }

            assertTrue(result is Err)
            assertEquals(BindingError, result.error)
        }
    }
}
