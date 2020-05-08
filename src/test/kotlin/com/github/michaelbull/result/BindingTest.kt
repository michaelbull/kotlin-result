package com.github.michaelbull.result

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BindingTest {

    object BindingError

    @Test
    fun returnsOkIfAllBindsSuccessful() {
        fun provideX(): Result<Int, BindingError> = Ok(1)
        fun provideY(): Result<Int, BindingError> = Ok(2)

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        assertTrue(result is Ok)
        assertEquals(3, result.value)
    }

    @Test
    fun returnsOkIfAllBindsOfDifferentTypeAreSuccessful() {
        fun provideX(): Result<String, BindingError> = Ok("1")
        fun provideY(x: Int): Result<Int, BindingError> = Ok(x + 2)

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY(x.toInt()).bind()
            y
        }

        assertTrue(result is Ok)
        assertEquals(3, result.value)
    }

    @Test
    fun returnsFirstErrIfBindingFailed() {
        fun provideX(): Result<Int, BindingError> = Ok(1)
        fun provideY(): Result<Int, BindingError> = Err(BindingError)
        fun provideZ(): Result<Int, BindingError> = Ok(2)

        val result = binding<Int, BindingError> {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertTrue(result is Err)
        assertEquals(BindingError, result.error)
    }

    @Test
    fun returnsFirstErrIfBindingsOfDifferentTypesFailed() {
        fun provideX(): Result<Int, BindingError> = Ok(1)
        fun provideY(): Result<String, BindingError> = Err(BindingError)
        fun provideZ(): Result<Int, BindingError> = Ok(2)

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
