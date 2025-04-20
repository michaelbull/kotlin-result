package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.and
import com.github.michaelbull.result.zip
import com.github.michaelbull.result.zipOrAccumulate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ParZipTest {

    data class ZipData3(val a: String, val b: Int, val c: Boolean)
    data class ZipData4(val a: String, val b: Int, val c: Boolean, val d: Double)
    data class ZipData5(val a: String, val b: Int, val c: Boolean, val d: Double, val e: Char)

    class Zip {

        @Test
        fun returnsTransformedValueIfBothOk() = runTest {
            val modifyGate = CompletableDeferred<Int>()

            val result = parZip(
                {
                    modifyGate.await()
                    delay(100)
                    Ok(10)
                },
                {
                    delay(100)
                    Ok(20).also { modifyGate.complete(0) }
                },
                { v1, v2 -> v1 + v2 }
            )

            assertEquals(
                expected = Ok(30),
                actual = result,
            )
        }
    }
}
