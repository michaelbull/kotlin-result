package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class IterableTest {
    private sealed interface IterableError {
        data object IterableError1 : IterableError
        data object IterableError2 : IterableError
    }

    class Fold {

        @Test
        fun returnAccumulatedValueIfOk() {
            val result = listOf(20, 30, 40, 50).fold(
                initial = 10,
                operation = { a, b -> Ok(a + b) },
            )

            assertEquals(
                expected = Ok(150),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrorIfErr() {
            val result: Result<Int, IterableError> = listOf(5, 10, 15, 20, 25).fold(
                initial = 1,
                operation = { a, b ->
                    when (b) {
                        (5 + 10) -> Err(IterableError.IterableError1)
                        (5 + 10 + 15 + 20) -> Err(IterableError.IterableError2)
                        else -> Ok(a * b)
                    }
                },
            )

            assertEquals(
                expected = Err(IterableError.IterableError1),
                actual = result,
            )
        }
    }

    class FoldRight {

        @Test
        fun returnsAccumulatedValueIfOk() {
            val result = listOf(2, 5, 10, 20).foldRight(
                initial = 100,
                operation = { a, b -> Ok(b - a) },
            )

            assertEquals(
                expected = Ok(63),
                actual = result,
            )
        }

        @Test
        fun returnsLastErrorIfErr() {
            val result = listOf(2, 5, 10, 20, 40).foldRight(
                initial = 38500,
                operation = { a, b ->
                    when (b) {
                        (((38500 / 40) / 20) / 10) -> Err(IterableError.IterableError1)
                        ((38500 / 40) / 20) -> Err(IterableError.IterableError2)
                        else -> Ok(b / a)
                    }
                },
            )

            assertEquals(
                expected = Err(IterableError.IterableError2),
                actual = result,
            )
        }
    }

    class Combine {

        @Test
        fun returnsValuesIfAllOk() {
            val result = combine(
                Ok(10),
                Ok(20),
                Ok(30),
            )

            assertEquals(
                expected = Ok(listOf(10, 20, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrorIfErr() {
            val result = combine(
                Ok(20),
                Ok(40),
                Err(IterableError.IterableError1),
                Ok(60),
                Err(IterableError.IterableError2),
                Ok(80),
            )

            assertEquals(
                expected = Err(IterableError.IterableError1),
                actual = result,
            )
        }
    }

    class ValuesOf {

        @Test
        fun returnsAllValues() {
            val result = valuesOf(
                Ok("hello"),
                Ok("big"),
                Err(IterableError.IterableError2),
                Ok("wide"),
                Err(IterableError.IterableError1),
                Ok("world"),
            )

            assertEquals(
                expected = listOf("hello", "big", "wide", "world"),
                actual = result
            )
        }
    }

    class ErrorsOf {

        @Test
        fun returnsAllErrors() {
            val result = errorsOf(
                Err(IterableError.IterableError2),
                Ok("haskell"),
                Err(IterableError.IterableError2),
                Ok("f#"),
                Err(IterableError.IterableError1),
                Ok("elm"),
                Err(IterableError.IterableError1),
                Ok("clojure"),
                Err(IterableError.IterableError2),
            )

            assertEquals(
                expected = listOf(
                    IterableError.IterableError2,
                    IterableError.IterableError2,
                    IterableError.IterableError1,
                    IterableError.IterableError1,
                    IterableError.IterableError2,
                ),
                actual = result
            )
        }
    }

    class Partition {

        @Test
        fun returnsPairOfValuesAndErrors() {
            val strings = listOf(
                "haskell",
                "f#",
                "elm",
                "clojure",
            )

            val errors = listOf(
                IterableError.IterableError2,
                IterableError.IterableError2,
                IterableError.IterableError1,
                IterableError.IterableError1,
                IterableError.IterableError2,
            )

            val result = partition(
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
                expected = Pair(strings, errors),
                actual = result,
            )
        }
    }

    class Traverse {
        @Test
        fun returnTraverseValueIfOk() {
            val input = listOf(1, 2, 3)
            val transform: (Int) -> Result<String, String> = { Ok(it.toString()) }
            val result = input.traverse(transform)

            assertEquals(Ok(listOf("1", "2", "3")), result)
        }

        @Test
        fun returnsFirstErrorIfErr() {
            val input = listOf(1, 2, 3)
            val transform: (Int) -> Result<String, String> = {
                if (it == 1) Err("Error at 1") else Ok(it.toString())
            }
            val result = input.traverse(transform)

            assertEquals(Err("Error at 1"), result)
        }

        @Test
        fun traverseWithEmptyListReturnsEmptyOkList() {
            val input = emptyList<Int>()
            val transform: (Int) -> Result<String, String> = { Ok(it.toString()) }
            val result = input.traverse(transform)

            assertEquals(Ok(emptyList()), result)
        }

    }
}
