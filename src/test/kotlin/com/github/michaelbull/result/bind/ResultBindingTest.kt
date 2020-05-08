package com.github.michaelbull.result.bind

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResultBindingTest {

    @Test
    fun `GIVEN a ResultBinding which contains all ok binds THEN return ok Result`() {
        val result = binding<Int, Exception> {
            val x = okIntResult().bind()
            val y = okIntResult().bind()
            x + y
        }

        assertTrue(result is Ok)
        assertEquals(2, result.value)
    }

    @Test
    fun `GIVEN a ResultBinding which contains all ok binds that are not of the same type WHEN the returning type matches the ResultBinding type THEN return ok Result`() {
        val result = binding<Int, Exception> {
            val x = okStringResult().bind()
            val y = Ok(x.toInt() + 2).bind()
            y
        }

        assertTrue(result is Ok)
        assertEquals(3, result.value)
    }

    @Test
    fun `GIVEN a ResultBinding which contains an err bind THEN return that bind's error in Result`() {
        val someException = Exception()
        val result = binding<Int, Exception> {
            val x = okIntResult().bind()
            val e = errIntResult(someException).bind()
            val y = okIntResult().bind()
            x + y
        }

        assertTrue(result is Err)
        assertEquals(someException, result.error)
    }

    @Test
    fun `GIVEN a ResultBinding which contains a function with an ok type not matching the ResultBinding WHEN it fails to bind THEN return that bind's error reason in Result`() {
        val someException = Exception()
        val result = binding<Int, Exception> {
            val x = okIntResult().bind()
            val e = errStringResult(someException).bind()
            val y = okIntResult().bind()
            x + y
        }

        assertTrue(result is Err)
        assertEquals(someException, result.error)
    }

    companion object {
        fun okIntResult(): Result<Int, Exception> = Ok(1)
        fun okStringResult(): Result<String, Exception> = Ok("1")
        fun errIntResult(exception: Exception): Result<Int, Exception> = Err(exception)
        fun errStringResult(exception: Exception): Result<String, Exception> = Err(exception)
    }
}
