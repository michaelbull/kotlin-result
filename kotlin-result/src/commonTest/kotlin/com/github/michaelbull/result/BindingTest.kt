package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class BindingTest {

    object BindingError

    @Test
    fun returnsOkIfAllBindsSuccessful() {
        fun provideX(): Result<Int, BindingError> = Ok(1)
        fun provideY(): Result<Int, BindingError> = Ok(2)

        val result = binding {
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
    fun returnsOkIfAllBindsOfDifferentTypeAreSuccessful() {
        fun provideX(): Result<String, BindingError> = Ok("1")
        fun provideY(x: Int): Result<Int, BindingError> = Ok(x + 2)

        val result = binding {
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
    fun returnsFirstErrIfBindingFailed() {
        fun provideX(): Result<Int, BindingError> = Ok(1)
        fun provideY(): Result<Int, BindingError> = Err(BindingError)
        fun provideZ(): Result<Int, BindingError> = Ok(2)

        val result = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result,
        )
    }

    @Test
    fun returnsFirstErrIfBindingsOfDifferentTypesFailed() {
        fun provideX(): Result<Int, BindingError> = Ok(1)
        fun provideY(): Result<String, BindingError> = Err(BindingError)
        fun provideZ(): Result<Int, BindingError> = Ok(2)

        val result: Result<Int, BindingError> = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y.toInt() + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result,
        )
    }
}
