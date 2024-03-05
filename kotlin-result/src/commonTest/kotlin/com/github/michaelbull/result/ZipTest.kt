package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

private fun produce(number: Int, error: String): Result<Int, String> {
    return Ok(number).and(Err(error))
}

class ZipTest {

    data class ZipData3(val a: String, val b: Int, val c: Boolean)
    data class ZipData4(val a: String, val b: Int, val c: Boolean, val d: Double)
    data class ZipData5(val a: String, val b: Int, val c: Boolean, val d: Double, val e: Char)

    class Zip {

        @Test
        fun returnsTransformedValueIfBothOk() {
            val result = zip(
                { Ok(10) },
                { Ok(20) },
                Int::plus,
            )

            assertEquals(
                expected = Ok(30),
                actual = result,
            )
        }

        @Test
        fun returnsErrIfOneOfTwoErr() {
            val result = zip(
                { Ok(10) },
                { produce(20, "hello") },
                Int::plus,
            )

            assertEquals(
                expected = Err("hello"),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfBothErr() {
            val result = zip(
                { produce(10, "foo") },
                { produce(20, "bar") },
                Int::plus,
            )

            assertEquals(
                expected = Err("foo"),
                actual = result,
            )
        }

        @Test
        fun returnsTransformedValueIfThreeOk() {
            val result = zip(
                { Ok("hello") },
                { Ok(2) },
                { Ok(false) },
                ::ZipData3,
            )

            assertEquals(
                expected = Ok(ZipData3("hello", 2, false)),
                actual = result,
            )
        }

        @Test
        fun returnsErrIfOneOfThreeErr() {
            val result = zip(
                { Ok("foo") },
                { Ok(1).and(Err("bar")) },
                { Ok(false) },
                ::ZipData3,
            )

            assertEquals(
                expected = Err("bar"),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTwoOfThreeErr() {
            val result = zip(
                { Ok("foo") },
                { Ok(1).and(Err("bar")) },
                { Ok(false).and(Err("baz")) },
                ::ZipData3,
            )

            assertEquals(
                expected = Err("bar"),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfAllThreeErr() {
            val result = zip(
                { Ok("foo").and(Err(1)) },
                { Ok(1).and(Err(2)) },
                { Ok(false).and(Err(3)) },
                ::ZipData3,
            )

            assertEquals(
                expected = Err(1),
                actual = result,
            )
        }

        @Test
        fun returnsTransformedValueIfFourOk() {
            val result = zip(
                { Ok("hello") },
                { Ok(2) },
                { Ok(false) },
                { Ok(1.5) },
                ::ZipData4,
            )

            assertEquals(
                expected = Ok(ZipData4("hello", 2, false, 1.5)),
                actual = result
            )
        }

        @Test
        fun returnsFirstErrIfSomeOfFourErr() {
            val result = zip(
                { Ok("hello") },
                { Ok(2).and(Err(1)) },
                { Ok(false) },
                { Ok(1.5).and(Err(2)) },
                ::ZipData4,
            )

            assertEquals(
                expected = Err(1),
                actual = result,
            )
        }

        @Test
        fun returnsTransformedValueIfFiveOk() {
            val result = zip(
                { Ok("hello") },
                { Ok(2) },
                { Ok(false) },
                { Ok(1.5) },
                { Ok('a') },
                ::ZipData5,
            )

            assertEquals(
                expected = Ok(ZipData5("hello", 2, false, 1.5, 'a')),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfSomeOfFiveErr() {
            val result = zip(
                { Ok("hello").and(Err(1)) },
                { Ok(2) },
                { Ok(false) },
                { Ok(1.5) },
                { Ok('a').and(Err(2)) },
                ::ZipData5,
            )

            assertEquals(
                expected = Err(1),
                actual = result,
            )
        }
    }

    class ZipOrAccumulate {

        @Test
        fun returnsTransformedValueIfAllOk() {
            val result = zipOrAccumulate(
                { Ok(10) },
                { Ok(20) },
                { Ok(30) },
                { Ok(40) },
                { Ok(50) },
            ) { a, b, c, d, e ->
                a + b + c + d + e
            }

            assertEquals(
                expected = Ok(150),
                actual = result,
            )
        }

        @Test
        fun returnsAllErrsIfAllErr() {
            val result = zipOrAccumulate(
                { produce(10, "error one") },
                { produce(20, "error two") },
                { produce(30, "error three") },
                { produce(40, "error four") },
                { produce(50, "error five") },
            ) { a, b, c, d, e ->
                a + b + c + d + e
            }

            val errors = listOf(
                "error one",
                "error two",
                "error three",
                "error four",
                "error five",
            )

            assertEquals(
                expected = Err(errors),
                actual = result,
            )
        }

        @Test
        fun returnsOneErrsIfOneOfErr() {
            val result = zipOrAccumulate(
                { Ok(10) },
                { produce(20, "only error") },
                { Ok(30) },
                { Ok(40) },
                { Ok(50) },
            ) { a, b, c, d, e ->
                a + b + c + d + e
            }

            assertEquals(
                expected = Err(listOf("only error")),
                actual = result,
            )
        }
    }
}
