package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class TryTest {
    private sealed interface TryError {
        data object TryError1 : TryError
        data object TryError2 : TryError
    }

    class TryFind {

        @Test
        fun returnsMatchingElement() {
            val result = listOf(1, 2, 3, 4).tryFind { element ->
                Ok(element == 3)
            }

            assertEquals(
                expected = Ok(3),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result = listOf(1, 2, 3, 4).tryFind { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element == 3)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }

        @Test
        fun returnsNullIfNoMatch() {
            val result = listOf(1, 2, 3).tryFind { element ->
                Ok(element == 99)
            }

            assertEquals(
                expected = null,
                actual = result,
            )
        }
    }

    class TryFindLast {

        @Test
        fun returnsLastMatchingElement() {
            val result = listOf(1, 2, 3, 4).tryFindLast { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(4),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result = listOf(1, 2, 3, 4).tryFindLast { element ->
                if (element == 3) {
                    Err("bad")
                } else {
                    Ok(element % 2 == 0)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }

        @Test
        fun returnsNullIfNoMatch() {
            val result = listOf(1, 2, 3).tryFindLast { element ->
                Ok(element == 99)
            }

            assertEquals(
                expected = null,
                actual = result,
            )
        }
    }

    class TryMap {

        @Test
        fun returnsTransformedValuesIfAllOk() {
            val result = listOf(1, 2, 3).tryMap { Ok(it * 10) }

            assertEquals(
                expected = Ok(listOf(10, 20, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<List<Int>, String> = listOf(1, 2, 3).tryMap { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element * 10)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapTo {

        @Test
        fun appendsTransformedValuesIfAllOk() {
            val destination = mutableListOf(0)

            val result = listOf(1, 2, 3).tryMapTo(destination) { Ok(it * 10) }

            assertEquals(
                expected = Ok(listOf(0, 10, 20, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableListOf(0)

            val result: Result<MutableList<Int>, String> = listOf(1, 2, 3).tryMapTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element * 10)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapNotNull {

        @Test
        fun returnsNonNullTransformedValuesIfAllOk() {
            val result = listOf(1, 2, 3).tryMapNotNull { element ->
                if (element == 2) {
                    null
                } else {
                    Ok(element * 10)
                }
            }

            assertEquals(
                expected = Ok(listOf(10, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<List<Int>, String> = listOf(1, 2, 3).tryMapNotNull { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element * 10)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapNotNullTo {

        @Test
        fun appendsNonNullTransformedValuesIfAllOk() {
            val destination = mutableListOf(0)

            val result = listOf(1, 2, 3).tryMapNotNullTo(destination) { element ->
                if (element == 2) {
                    null
                } else {
                    Ok(element * 10)
                }
            }

            assertEquals(
                expected = Ok(listOf(0, 10, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableListOf(0)

            val result: Result<MutableList<Int>, String> = listOf(1, 2, 3).tryMapNotNullTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element * 10)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapIndexed {

        @Test
        fun returnsTransformedValuesIfAllOk() {
            val result = listOf(10, 20, 30).tryMapIndexed { index, value -> Ok(index to value) }

            assertEquals(
                expected = Ok(listOf(0 to 10, 1 to 20, 2 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<List<Pair<Int, Int>>, String> = listOf(10, 20, 30).tryMapIndexed { index, _ ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(index to index)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapIndexedTo {

        @Test
        fun appendsTransformedValuesIfAllOk() {
            val destination = mutableListOf(-1 to -1)

            val result = listOf(10, 20, 30).tryMapIndexedTo(destination) { index, value -> Ok(index to value) }

            assertEquals(
                expected = Ok(listOf(-1 to -1, 0 to 10, 1 to 20, 2 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableListOf(-1 to -1)

            val result: Result<MutableList<Pair<Int, Int>>, String> = listOf(10, 20, 30).tryMapIndexedTo(destination) { index, _ ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(index to index)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapIndexedNotNull {

        @Test
        fun returnsNonNullTransformedValuesIfAllOk() {
            val result = listOf(10, 20, 30).tryMapIndexedNotNull { index, value ->
                if (index == 1) {
                    null
                } else {
                    Ok(index to value)
                }
            }

            assertEquals(
                expected = Ok(listOf(0 to 10, 2 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<List<Pair<Int, Int>>, String> = listOf(10, 20, 30).tryMapIndexedNotNull { index, _ ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(index to index)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryMapIndexedNotNullTo {

        @Test
        fun appendsNonNullTransformedValuesIfAllOk() {
            val destination = mutableListOf(-1 to -1)

            val result = listOf(10, 20, 30).tryMapIndexedNotNullTo(destination) { index, value ->
                if (index == 1) {
                    null
                } else {
                    Ok(index to value)
                }
            }

            assertEquals(
                expected = Ok(listOf(-1 to -1, 0 to 10, 2 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableListOf(-1 to -1)

            val result: Result<MutableList<Pair<Int, Int>>, String> = listOf(10, 20, 30).tryMapIndexedNotNullTo(destination) { index, _ ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(index to index)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFold {

        @Test
        fun returnAccumulatedValueIfOk() {
            val result = listOf(20, 30, 40, 50).tryFold(
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
            val result: Result<Int, TryError> = listOf(5, 10, 15, 20, 25).tryFold(
                initial = 1,
                operation = { a, b ->
                    when (b) {
                        (5 + 10) -> Err(TryError.TryError1)
                        (5 + 10 + 15 + 20) -> Err(TryError.TryError2)
                        else -> Ok(a * b)
                    }
                },
            )

            assertEquals(
                expected = Err(TryError.TryError1),
                actual = result,
            )
        }
    }

    class TryFoldRight {

        @Test
        fun returnsAccumulatedValueIfOk() {
            val result = listOf(2, 5, 10, 20).tryFoldRight(
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
            val result = listOf(2, 5, 10, 20, 40).tryFoldRight(
                initial = 38500,
                operation = { a, b ->
                    when (b) {
                        (((38500 / 40) / 20) / 10) -> Err(TryError.TryError1)
                        ((38500 / 40) / 20) -> Err(TryError.TryError2)
                        else -> Ok(b / a)
                    }
                },
            )

            assertEquals(
                expected = Err(TryError.TryError2),
                actual = result,
            )
        }
    }

    class TryForEach {

        @Test
        fun returnsOkIfAllActionsSucceed() {
            val collected = mutableListOf<Int>()

            val result = listOf(1, 2, 3).tryForEach { element ->
                collected.add(element)
                Ok(Unit)
            }

            assertEquals(
                expected = Ok(Unit),
                actual = result,
            )

            assertEquals(
                expected = listOf(1, 2, 3),
                actual = collected,
            )
        }

        @Test
        fun returnsFirstErrIfActionFails() {
            val collected = mutableListOf<Int>()

            val result: Result<Unit, String> = listOf(1, 2, 3).tryForEach { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    collected.add(element)
                    Ok(Unit)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )

            assertEquals(
                expected = listOf(1),
                actual = collected,
            )
        }
    }

    class TryForEachIndexed {

        @Test
        fun returnsOkIfAllActionsSucceed() {
            val collected = mutableListOf<Pair<Int, Int>>()

            val result = listOf(10, 20, 30).tryForEachIndexed { index, value ->
                collected.add(index to value)
                Ok(Unit)
            }

            assertEquals(
                expected = Ok(Unit),
                actual = result,
            )

            assertEquals(
                expected = listOf(0 to 10, 1 to 20, 2 to 30),
                actual = collected,
            )
        }

        @Test
        fun returnsFirstErrIfActionFails() {
            val collected = mutableListOf<Pair<Int, Int>>()

            val result: Result<Unit, String> = listOf(10, 20, 30).tryForEachIndexed { index, value ->
                if (index == 1) {
                    Err("bad")
                } else {
                    collected.add(index to value)
                    Ok(Unit)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )

            assertEquals(
                expected = listOf(0 to 10),
                actual = collected,
            )
        }
    }

    class TryReduce {

        @Test
        fun returnsReducedValueIfOk() {
            val result = listOf(1, 2, 3, 4).tryReduce { acc, element ->
                Ok(acc + element)
            }

            assertEquals(
                expected = Ok(10),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfOperationFails() {
            val result = listOf(1, 2, 3, 4).tryReduce { acc, element ->
                if (element == 3) {
                    Err("bad")
                } else {
                    Ok(acc + element)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }

        @Test
        fun returnsNullIfEmpty() {
            val result = emptyList<Int>().tryReduce { acc, element ->
                Ok(acc + element)
            }

            assertEquals(
                expected = null,
                actual = result,
            )
        }
    }

    class TryReduceIndexed {

        @Test
        fun returnsReducedValueWithIndicesIfOk() {
            val collected = mutableListOf<Int>()

            val result = listOf(10, 20, 30).tryReduceIndexed { index, acc, element ->
                collected.add(index)
                Ok(acc + element)
            }

            assertEquals(
                expected = Ok(60),
                actual = result,
            )

            assertEquals(
                expected = listOf(1, 2),
                actual = collected,
            )
        }

        @Test
        fun returnsFirstErrIfOperationFails() {
            val result = listOf(10, 20, 30).tryReduceIndexed { index, acc, element ->
                if (index == 2) {
                    Err("bad")
                } else {
                    Ok(acc + element)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }

        @Test
        fun returnsNullIfEmpty() {
            val result = emptyList<Int>().tryReduceIndexed { _, acc, element ->
                Ok(acc + element)
            }

            assertEquals(
                expected = null,
                actual = result,
            )
        }
    }
}
