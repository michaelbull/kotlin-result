package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class IterableTest {
    private sealed class IterableError {
        object IterableError1 : IterableError()
        object IterableError2 : IterableError()
    }

    internal class `fold` {
        @Test
        internal fun returnAccumulatedValueIfOk() {
            val result = listOf(20, 30, 40, 50).fold(
                initial = 10,
                operation = { a, b -> Ok(a + b) }
            )

            result as Ok

            assertEquals(
                expected = 150,
                actual = result.value
            )
        }

        @Test
        internal fun returnsFirstErrorIfErr() {
            val result: Result<Int, IterableError> = listOf(5, 10, 15, 20, 25).fold(
                initial = 1,
                operation = { a, b ->
                    when (b) {
                        (5 + 10) -> Err(IterableError.IterableError1)
                        (5 + 10 + 15 + 20) -> Err(IterableError.IterableError2)
                        else -> Ok(a * b)
                    }
                }
            )

            result as Err

            assertSame(
                expected = IterableError.IterableError1,
                actual = result.error
            )
        }
    }

    internal class `foldRight` {
        @Test
        internal fun returnsAccumulatedValueIfOk() {
            val result = listOf(2, 5, 10, 20).foldRight(
                initial = 100,
                operation = { a, b -> Ok(b - a) }
            )

            result as Ok

            assertEquals(
                expected = 63,
                actual = result.value
            )
        }

        @Test
        internal fun returnsLastErrorIfErr() {
            val result = listOf(2, 5, 10, 20, 40).foldRight(
                initial = 38500,
                operation = { a, b ->
                    when (b) {
                        (((38500 / 40) / 20) / 10) -> Err(IterableError.IterableError1)
                        ((38500 / 40) / 20) -> Err(IterableError.IterableError2)
                        else -> Ok(b / a)
                    }
                }
            )

            result as Err

            assertSame(
                expected = IterableError.IterableError2,
                actual = result.error
            )
        }
    }

    internal class `combine` {
        @Test
        internal fun returnsValuesIfAllOk() {
            val values = combine(
                Ok(10),
                Ok(20),
                Ok(30)
            ).get()!!

            assertEquals(
                expected = 3,
                actual = values.size
            )

            assertEquals(
                expected = 10,
                actual = values[0]
            )

            assertEquals(
                expected = 20,
                actual = values[1]
            )

            assertEquals(
                expected = 30,
                actual = values[2]
            )
        }

        @Test
        internal fun returnsFirstErrorIfErr() {
            val result = combine(
                Ok(20),
                Ok(40),
                Err(IterableError.IterableError1),
                Ok(60),
                Err(IterableError.IterableError2),
                Ok(80)
            )

            result as Err

            assertSame(
                expected = IterableError.IterableError1,
                actual = result.error
            )
        }
    }

    internal class `getAll` {
        @Test
        internal fun returnsAllValues() {
            val values = getAll(
                Ok("hello"),
                Ok("big"),
                Err(IterableError.IterableError2),
                Ok("wide"),
                Err(IterableError.IterableError1),
                Ok("world")
            )

            assertEquals(
                expected = 4,
                actual = values.size
            )

            assertEquals(
                expected = "hello",
                actual = values[0]
            )

            assertEquals(
                expected = "big",
                actual = values[1]
            )

            assertEquals(
                expected = "wide",
                actual = values[2]
            )

            assertEquals(
                expected = "world",
                actual = values[3]
            )
        }
    }

    internal class `getAllErrors` {
        @Test
        internal fun returnsAllErrors() {
            val errors = getAllErrors(
                Err(IterableError.IterableError2),
                Ok("haskell"),
                Err(IterableError.IterableError2),
                Ok("f#"),
                Err(IterableError.IterableError1),
                Ok("elm"),
                Err(IterableError.IterableError1),
                Ok("clojure"),
                Err(IterableError.IterableError2)
            )

            assertEquals(
                expected = 5,
                actual = errors.size
            )

            assertSame(
                expected = IterableError.IterableError2,
                actual = errors[0]
            )

            assertSame(
                expected = IterableError.IterableError2,
                actual = errors[1]
            )

            assertSame(
                expected = IterableError.IterableError1,
                actual = errors[2]
            )

            assertSame(
                expected = IterableError.IterableError1,
                actual = errors[3]
            )

            assertSame(
                expected = IterableError.IterableError2,
                actual = errors[4]
            )
        }
    }

    internal class `partition` {
        @Test
        internal fun returnsPairOfValuesAndErrors() {
            val pairs = partition(
                Err(IterableError.IterableError2),
                Ok("haskell"),
                Err(IterableError.IterableError2),
                Ok("f#"),
                Err(IterableError.IterableError1),
                Ok("elm"),
                Err(IterableError.IterableError1),
                Ok("clojure"),
                Err(IterableError.IterableError2)
            )

            val values = pairs.first

            assertEquals(
                expected = 4,
                actual = values.size
            )

            assertEquals(
                expected = "haskell",
                actual = values[0]
            )

            assertEquals(
                expected = "f#",
                actual = values[1]
            )

            assertEquals(
                expected = "elm",
                actual = values[2]
            )

            assertEquals(
                expected = "clojure",
                actual = values[3]
            )

            val errors = pairs.second

            assertEquals(
                expected = 5,
                actual = errors.size
            )

            assertSame(
                expected = IterableError.IterableError2,
                actual = errors[0]
            )

            assertSame(
                expected = IterableError.IterableError2,
                actual = errors[1]
            )

            assertSame(
                expected = IterableError.IterableError1,
                actual = errors[2]
            )

            assertSame(
                expected = IterableError.IterableError1,
                actual = errors[3]
            )

            assertSame(
                expected = IterableError.IterableError2,
                actual = errors[4]
            )
        }
    }
}
