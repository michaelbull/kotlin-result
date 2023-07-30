package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class ZipOrAccumulateTest {

    class ZipOrAccumulate {

        @Test
        fun returnsTransformedValueIfAllOk() {
            val result = zipOrAccumulate(
                { Ok(10) },
                { Ok(20) },
                { Ok(30) },
                { Ok(40) },
                { Ok(50) },
                { Ok(60) },
                { Ok(70) },
                { Ok(80) },
                { Ok(90) },
                { Ok(100) },
            ) { a, b, c, d, e, f, g, h, i, j ->
                a + b + c + d + e + f + g + h + i + j
            }

            result as Ok

            assertEquals(
                expected = 550,
                actual = result.value,
            )
        }

        @Test
        fun returnsAllErrsIfAllErr() {
            val result = zipOrAccumulate(
                { Ok(10).and(Err("10")) },
                { Ok(20).and(Err("20")) },
                { Ok(30).and(Err("30")) },
                { Ok(40).and(Err("40")) },
                { Ok(50).and(Err("50")) },
                { Ok(60).and(Err("60")) },
                { Ok(70).and(Err("70")) },
                { Ok(80).and(Err("80")) },
                { Ok(90).and(Err("90")) },
                { Ok(100).and(Err("100")) },
            ) { a, b, c, d, e, f, g, h, i, j ->
                a + b + c + d + e + f + g + h + i + j
            }

            result as Err

            assertEquals(
                expected = listOf(
                    "10",
                    "20",
                    "30",
                    "40",
                    "50",
                    "60",
                    "70",
                    "80",
                    "90",
                    "100",
                ),
                actual = result.error,
            )
        }

        @Test
        fun returnsOneErrsIfOneOfErr() {
            val result = zipOrAccumulate(
                { Ok(10) },
                { Ok(20).and(Err("error")) },
                { Ok(30) },
                { Ok(40) },
                { Ok(50) },
                { Ok(60) },
                { Ok(70) },
                { Ok(80) },
                { Ok(90) },
                { Ok(100) },
            ) { a, b, c, d, e, f, g, h, i, j ->
                a + b + c + d + e + f + g + h + i + j
            }

            result as Err

            assertEquals(
                expected = listOf(
                    "error",
                ),
                actual = result.error,
            )
        }
    }
}
