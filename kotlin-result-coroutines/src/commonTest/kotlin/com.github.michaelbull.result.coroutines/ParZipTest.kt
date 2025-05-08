package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals

private suspend inline fun simulateDelay() = delay(100)

private suspend fun <V> produceErr(error: String): Result<V, String> {
    simulateDelay()
    return Err(error)
}

private suspend fun <V> produceOk(value: V): Result<V, String> {
    simulateDelay()
    return Ok(value)
}

class ParZipTest {

    data class ZipData3(val a: String, val b: Int, val c: Boolean)
    data class ZipData4(val a: String, val b: Int, val c: Boolean, val d: Double)
    data class ZipData5(val a: String, val b: Int, val c: Boolean, val d: Double, val e: Char)

    @Test
    fun parZip2ReturnsTransformedValueIfBothOk() = runTest {
        val modifyGate = CompletableDeferred<Unit>()

        val result = withContext(Dispatchers.Default) {
            parZip(
                {
                    modifyGate.await()
                    produceOk(value = "producer1")
                },
                {
                    modifyGate.complete(Unit)
                    produceOk(value = "producer2")
                },
                { v1, v2 ->
                    simulateDelay()
                    v1 to v2
                }
            )
        }

        assertEquals(
            expected = Ok("producer1" to "producer2"),
            actual = result,
        )
    }

    @Test
    fun parZip2ReturnsErrIfOneOfTwoErr() = runTest {
        val modifyGate = CompletableDeferred<Unit>()

        val result = withContext(Dispatchers.Default) {
            parZip(
                {
                    modifyGate.await()
                    produceOk(value = "producer1")
                },
                {
                    modifyGate.complete(Unit)
                    produceErr<Int>(error = "failed")
                },
                { v1, v2 ->
                    simulateDelay()
                    v1 to v2
                }
            )
        }

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZip3ReturnsTransformedValueIfAllOk() = runTest {
        val result = withContext(Dispatchers.Default) {
            parZip(
                { produceOk(value = "producer1") },
                { produceOk(value = 42) },
                { produceOk(value = true) },
                { v1, v2, v3 ->
                    simulateDelay()
                    ZipData3(v1, v2, v3)
                }
            )
        }

        assertEquals(
            expected = Ok(ZipData3("producer1", 42, true)),
            actual = result,
        )
    }

    @Test
    fun parZip3ReturnsErrIfOneOfThreeErr() = runTest {
        val result = withContext(Dispatchers.Default) {
            parZip(
                { produceOk(value = "producer1") },
                { produceErr<Int>(error = "failed") },
                { produceOk(value = true) },
                { v1, v2, v3 ->
                    simulateDelay()
                    ZipData3(v1, v2, v3)
                }
            )
        }

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZip4ReturnsTransformedValueIfAllOk() = runTest {
        val result = withContext(Dispatchers.Default) {
            parZip(
                { produceOk(value = "producer1") },
                { produceOk(value = 42) },
                { produceOk(value = true) },
                { produceOk(value = 3.14) },
                { v1, v2, v3, v4 ->
                    simulateDelay()
                    ZipData4(v1, v2, v3, v4)
                }
            )
        }

        assertEquals(
            expected = Ok(ZipData4("producer1", 42, true, 3.14)),
            actual = result,
        )
    }

    @Test
    fun parZip4ReturnsErrIfOneOfFourErr() = runTest {
        val result = withContext(Dispatchers.Default) {
            parZip(
                { produceOk(value = "producer1") },
                { produceErr<Int>(error = "failed") },
                { produceOk(value = true) },
                { produceOk(value = 3.14) },
                { v1, v2, v3, v4 ->
                    simulateDelay()
                    ZipData4(v1, v2, v3, v4)
                }
            )
        }

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }

    @Test
    fun parZip5ReturnsTransformedValueIfAllOk() = runTest {
        val result = withContext(Dispatchers.Default) {
            parZip(
                { produceOk(value = "producer1") },
                { produceOk(value = 42) },
                { produceOk(value = true) },
                { produceOk(value = 3.14) },
                { produceOk(value = 'X') },
                { v1, v2, v3, v4, v5 ->
                    simulateDelay()
                    ZipData5(v1, v2, v3, v4, v5)
                }
            )
        }

        assertEquals(
            expected = Ok(ZipData5("producer1", 42, true, 3.14, 'X')),
            actual = result,
        )
    }

    @Test
    fun parZip5ReturnsErrIfOneOfFiveErr() = runTest {
        val result = withContext(Dispatchers.Default) {
            parZip(
                { produceOk(value = "producer1") },
                { produceErr<Int>(error = "failed") },
                { produceOk(value = true) },
                { produceOk(value = 3.14) },
                { produceOk(value = 'X') },
                { v1, v2, v3, v4, v5 ->
                    simulateDelay()
                    ZipData5(v1, v2, v3, v4, v5)
                }
            )
        }

        assertEquals(
            expected = Err("failed"),
            actual = result,
        )
    }
}
