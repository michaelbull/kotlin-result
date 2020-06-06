package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

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
                Int::plus
            )

            result as Ok

            assertEquals(
                expected = 30,
                actual = result.value
            )
        }

        @Test
        fun returnsErrIfOneOfTwoErr() {
            val result = zip(
                { Ok(10) },
                { Ok(20).and { Err("hello") } },
                Int::plus
            )

            result as Err

            assertEquals(
                expected = "hello",
                actual = result.error
            )
        }

        @Test
        fun returnsFirstErrIfBothErr() {
            val result = zip(
                { Ok(10).and { Err("foo") } },
                { Ok(20).and { Err("bar") } },
                Int::plus
            )

            result as Err

            assertEquals(
                expected = "foo",
                actual = result.error
            )
        }

        @Test
        fun returnsTransformedValueIfThreeOk() {
            val result = zip(
                { Ok("hello") },
                { Ok(2) },
                { Ok(false) },
                ::ZipData3
            )

            result as Ok

            assertEquals(
                expected = ZipData3("hello", 2, false),
                actual = result.value
            )
        }

        @Test
        fun returnsErrIfOneOfThreeErr() {
            val result = zip(
                { Ok("foo") },
                { Ok(1).and { Err("bar") } },
                { Ok(false) },
                ::ZipData3
            )

            result as Err

            assertEquals(
                expected = "bar",
                actual = result.error
            )
        }

        @Test
        fun returnsFirstErrIfTwoOfThreeErr() {
            val result = zip(
                { Ok("foo") },
                { Ok(1).and { Err("bar") } },
                { Ok(false).and { Err("baz") } },
                ::ZipData3
            )

            assertEquals(
                expected = "bar",
                actual = result.getError()
            )
        }

        @Test
        fun returnsFirstErrIfAllThreeErr() {
            val result = zip(
                { Ok("foo").and { Err(1) } },
                { Ok(1).and { Err(2) } },
                { Ok(false).and { Err(3) } },
                ::ZipData3
            )

            result as Err

            assertEquals(
                expected = 1,
                actual = result.error
            )
        }

        @Test
        fun returnsTransformedValueIfFourOk() {
            val result = zip(
                { Ok("hello") },
                { Ok(2) },
                { Ok(false) },
                { Ok(1.5) },
                ::ZipData4
            )

            result as Ok

            assertEquals(
                expected = ZipData4("hello", 2, false, 1.5),
                actual = result.value
            )
        }

        @Test
        fun returnsFirstErrIfSomeOfFourErr() {
            val result = zip(
                { Ok("hello") },
                { Ok(2).and { Err(1) } },
                { Ok(false) },
                { Ok(1.5).and { Err(2) } },
                ::ZipData4
            )

            result as Err

            assertEquals(
                expected = 1,
                actual = result.error
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
                ::ZipData5
            )

            result as Ok

            assertEquals(
                expected = ZipData5("hello", 2, false, 1.5, 'a'),
                actual = result.value
            )
        }

        @Test
        fun returnsFirstErrIfSomeOfFiveErr() {
            val result = zip(
                { Ok("hello").and { Err(1) } },
                { Ok(2) },
                { Ok(false) },
                { Ok(1.5) },
                { Ok('a').and { Err(2) } },
                ::ZipData5
            )

            result as Err

            assertEquals(
                expected = 1,
                actual = result.error
            )
        }
    }
}
