package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ParZipTest {

    data class ZipData3(val a: String, val b: Int, val c: Boolean)
    data class ZipData4(val a: String, val b: Int, val c: Boolean, val d: Double)
    data class ZipData5(val a: String, val b: Int, val c: Boolean, val d: Double, val e: Char)

    @Test
    fun parZip2ReturnsTransformedValueIfBothOk() = runTest {
        val modifyGate = CompletableDeferred<Unit>()

        val result = parZip(
            {
                modifyGate.await()
                delay(100)
                Ok(value = "producer1")
            },
            {
                modifyGate.complete(Unit)
                delay(100)
                Ok(value = "producer2")
            },
            { v1, v2 ->
                delay(100)
                v1 to v2
            }
        )

        assertEquals(
            expected = Ok("producer1" to "producer2"),
            actual = result,
        )
    }

    @Test
    fun parZip2ReturnsErrIfOneOfTwoErr() = runTest {
        val modifyGate = CompletableDeferred<Unit>()

        val result = parZip(
            {
                modifyGate.await()
                delay(100)
                Ok(value = "producer1")
            },
            {
                modifyGate.complete(Unit)
                delay(100)
                Err(error = "failed")
            },
            { v1, v2 ->
                delay(100)
                v1 to v2
            }
        )

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZip3ReturnsTransformedValueIfAllOk() = runTest {
        val result = parZip(
            {
                delay(100)
                Ok(value = "producer1")
            },
            {
                delay(100)
                Ok(value = 42)
            },
            {
                delay(100)
                Ok(value = true)
            },
            { v1, v2, v3 ->
                delay(100)
                ZipData3(v1, v2, v3)
            }
        )

        assertEquals(
            expected = Ok(ZipData3("producer1", 42, true)),
            actual = result,
        )
    }

    @Test
    fun parZip3ReturnsErrIfOneOfThreeErr() = runTest {
        val result = parZip(
            {
                delay(100)
                Ok(value = "producer1")
            },
            {
                delay(100)
                Err(error = "failed")
            },
            {
                delay(100)
                Ok(value = true)
            },
            { v1, v2, v3 ->
                delay(100)
                ZipData3(v1, v2, v3)
            }
        )

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZip4ReturnsTransformedValueIfAllOk() = runTest {
        val result = parZip(
            {
                delay(100)
                Ok(value = "producer1")
            },
            {
                delay(100)
                Ok(value = 42)
            },
            {
                delay(100)
                Ok(value = true)
            },
            {
                delay(100)
                Ok(value = 3.14)
            },
            { v1, v2, v3, v4 ->
                delay(100)
                ZipData4(v1, v2, v3, v4)
            }
        )

        assertEquals(
            expected = Ok(ZipData4("producer1", 42, true, 3.14)),
            actual = result,
        )
    }

    @Test
    fun parZip4ReturnsErrIfOneOfFourErr() = runTest {
        val result = parZip(
            {
                delay(100)
                Ok(value = "producer1")
            },
            {
                delay(100)
                Err(error = "failed")
            },
            {
                delay(100)
                Ok(value = true)
            },
            {
                delay(100)
                Ok(value = 3.14)
            },
            { v1, v2, v3, v4 ->
                delay(100)
                ZipData4(v1, v2, v3, v4)
            }
        )


        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZip5ReturnsTransformedValueIfAllOk() = runTest {
        val result = parZip(
            {
                delay(100)
                Ok(value = "producer1")
            },
            {
                delay(100)
                Ok(value = 42)
            },
            {
                delay(100)
                Ok(value = true)
            },
            {
                delay(100)
                Ok(value = 3.14)
            },
            {
                delay(100)
                Ok(value = 'X')
            },
            { v1, v2, v3, v4, v5 ->
                delay(100)
                ZipData5(v1, v2, v3, v4, v5)
            }
        )

        assertEquals(
            expected = Ok(ZipData5("producer1", 42, true, 3.14, 'X')),
            actual = result,
        )
    }

    @Test
    fun parZip5ReturnsErrIfOneOfFiveErr() = runTest {
        val result = parZip(
            {
                delay(100)
                Ok(value = "producer1")
            },
            {
                delay(100)
                Err(error = "failed")
            },
            {
                delay(100)
                Ok(value = true)
            },
            {
                delay(100)
                Ok(value = 3.14)
            },
            {
                delay(100)
                Ok(value = 'X')
            },
            { v1, v2, v3, v4, v5 ->
                delay(100)
                ZipData5(v1, v2, v3, v4, v5)
            }
        )

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZipCancelsOtherProducers() = runTest {
        var mutable = "zero"

        val job = launch {
            val result = parZip(
                {
                    delay(500)
                    mutable = "one"
                    Ok(value = "producer1")
                },
                {
                    delay(1000)
                    mutable = "two"
                    Err(error = "failed")
                },
                {
                    delay(1500)
                    mutable = "three"
                    Ok(value = true)
                },
                {
                    delay(2000)
                    mutable = "four"
                    Ok(value = 3.14)
                },
                {
                    delay(2500)
                    mutable = "five"
                    Ok(value = 'X')
                },
                { v1, v2, v3, v4, v5 ->
                    delay(3000)
                    ZipData5(v1, v2, v3, v4, v5)
                }
            )

            assertEquals(
                expected = Err("failed"),
                actual = result,
            )
        }

        advanceTimeBy(5000)
        job.cancel()

        assertEquals(
            expected = "two",
            actual = mutable,
        )
    }
}
