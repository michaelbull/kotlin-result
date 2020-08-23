package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.*
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.*

class AsyncSuspendableBindingTest {

    private sealed class BindingError {
        object BindingErrorA: BindingError()
        object BindingErrorB: BindingError()
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
            delay(1)
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

        runBlocking{
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
}
