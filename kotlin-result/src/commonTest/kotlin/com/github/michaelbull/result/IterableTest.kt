package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class IterableTest {
    private sealed interface IterableError {
        data object IterableError1 : IterableError
        data object IterableError2 : IterableError
    }

    class AllOk {

        @Test
        fun returnsTrueIfAllOk() {
            val result = listOf(
                Ok(1),
                Ok(2),
                Ok(3),
            ).allOk()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAnyErr() {
            val result = listOf(
                Ok(1),
                Err("error"),
                Ok(3),
            ).allOk()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class AllErr {

        @Test
        fun returnsTrueIfAllErr() {
            val result = listOf(
                Err("a"),
                Err("b"),
                Err("c"),
            ).allErr()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAnyOk() {
            val result = listOf(
                Err("a"),
                Ok(1),
                Err("c"),
            ).allErr()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class AnyOk {

        @Test
        fun returnsTrueIfAnyOk() {
            val result = listOf(
                Err("a"),
                Ok(1),
                Err("b"),
            ).anyOk()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAllErr() {
            val result = listOf(
                Err("a"),
                Err("b"),
            ).anyOk()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class AnyErr {

        @Test
        fun returnsTrueIfAnyErr() {
            val result = listOf(
                Ok(1),
                Err("a"),
                Ok(2),
            ).anyErr()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAllOk() {
            val result = listOf(
                Ok(1),
                Ok(2),
            ).anyErr()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class CountOk {

        @Test
        fun countsOkElements() {
            val result = listOf(
                Ok(1),
                Err("a"),
                Ok(2),
                Err("b"),
                Ok(3),
            ).countOk()

            assertEquals(
                expected = 3,
                actual = result,
            )
        }
    }

    class CountErr {

        @Test
        fun countsErrElements() {
            val result = listOf(
                Ok(1),
                Err("a"),
                Ok(2),
                Err("b"),
                Ok(3),
            ).countErr()

            assertEquals(
                expected = 2,
                actual = result,
            )
        }
    }

    class FilterOk {

        @Test
        fun returnsOnlyOkValues() {
            val result = listOf(
                Ok(1),
                Err("error"),
                Ok(2),
                Err("another"),
                Ok(3),
            ).filterOk()

            assertEquals(
                expected = listOf(1, 2, 3),
                actual = result,
            )
        }

        @Test
        fun returnsEmptyListIfAllErr() {
            val result = listOf(
                Err("a"),
                Err("b"),
            ).filterOk()

            assertEquals(
                expected = emptyList(),
                actual = result,
            )
        }
    }

    class FilterErr {

        @Test
        fun returnsOnlyErrValues() {
            val result = listOf(
                Ok(1),
                Err("error"),
                Ok(2),
                Err("another"),
            ).filterErr()

            assertEquals(
                expected = listOf("error", "another"),
                actual = result,
            )
        }

        @Test
        fun returnsEmptyListIfAllOk() {
            val result = listOf(Ok(1), Ok(2)).filterErr()

            assertEquals(
                expected = emptyList(),
                actual = result,
            )
        }
    }

    class FilterOkTo {

        @Test
        fun appendsOkValuesToDestination() {
            val destination = mutableListOf(0)

            listOf(
                Ok(1),
                Err("error"),
                Ok(2),
            ).filterOkTo(destination)

            assertEquals(
                expected = listOf(0, 1, 2),
                actual = destination,
            )
        }
    }

    class FilterErrTo {

        @Test
        fun appendsErrValuesToDestination() {
            val destination = mutableListOf("existing")

            listOf(
                Ok(1),
                Err("error"),
                Ok(2),
                Err("another"),
            ).filterErrTo(destination)

            assertEquals(
                expected = listOf("existing", "error", "another"),
                actual = destination,
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
            val result: Result<Int, IterableError> = listOf(5, 10, 15, 20, 25).tryFold(
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

    class OnEachOk {

        @Test
        fun invokesActionForOkResults() {
            val collected = mutableListOf<Int>()

            listOf(
                Ok(1),
                Err("error"),
                Ok(2),
            ).onEachOk(collected::add)

            assertEquals(
                expected = listOf(1, 2),
                actual = collected,
            )
        }

        @Test
        fun returnsIterableUnmodified() {
            val items = listOf(
                Ok(1),
                Err("error"),
                Ok(2),
            )

            val result = items.onEachOk { }

            assertEquals(
                expected = items,
                actual = result,
            )
        }
    }

    class OnEachOkIndexed {

        @Test
        fun invokesActionWithIndexForOkResults() {
            val collected = mutableListOf<Pair<Int, Int>>()

            listOf(
                Ok(10),
                Err("error"),
                Ok(20),
                Err("error"),
                Ok(30),
            ).onEachOkIndexed { index, value -> collected.add(index to value) }

            assertEquals(
                expected = listOf(0 to 10, 2 to 20, 4 to 30),
                actual = collected,
            )
        }
    }

    class OnEachErr {

        @Test
        fun invokesActionForErrResults() {
            val collected = mutableListOf<String>()

            listOf(
                Ok(1),
                Err("error1"),
                Ok(2),
                Err("error2"),
            ).onEachErr(collected::add)

            assertEquals(
                expected = listOf("error1", "error2"),
                actual = collected,
            )
        }

        @Test
        fun returnsIterableUnmodified() {
            val items = listOf(
                Ok(1),
                Err("error"),
                Ok(2),
            )

            val result = items.onEachErr { }

            assertEquals(
                expected = items,
                actual = result,
            )
        }
    }

    class OnEachErrIndexed {

        @Test
        fun invokesActionWithIndexForErrResults() {
            val collected = mutableListOf<Pair<Int, String>>()

            listOf(
                Ok(1),
                Err("error1"),
                Ok(2),
                Err("error2"),
                Ok(3),
            ).onEachErrIndexed { index, error -> collected.add(index to error) }

            assertEquals(
                expected = listOf(1 to "error1", 3 to "error2"),
                actual = collected,
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
                Err(IterableError.IterableError2),
            )

            assertEquals(
                expected = Pair(strings, errors),
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

        @Test
        fun returnsOkEmptyListIfEmpty() {
            val result = emptyList<Result<Int, IterableError>>()
                .combine()

            assertEquals(
                expected = Ok(emptyList()),
                actual = result,
            )
        }
    }

    class CombineTo {

        @Test
        fun returnsValuesIfAllOk() {
            val result: Result<Collection<Int>, IterableError> = listOf(
                Ok(10),
                Ok(20),
                Ok(30),
            ).combineTo(mutableListOf())

            assertEquals(
                expected = Ok(listOf(10, 20, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrorIfErr() {
            val result: Result<Collection<Int>, IterableError> = listOf(
                Ok(20),
                Ok(40),
                Err(IterableError.IterableError1),
                Ok(60),
                Err(IterableError.IterableError2),
                Ok(80),
            ).combineTo(mutableListOf())

            assertEquals(
                expected = Err(IterableError.IterableError1),
                actual = result,
            )
        }

        @Test
        fun returnsOkEmptyCollectionIfEmpty() {
            val result: Result<Collection<Int>, IterableError> = emptyList<Result<Int, IterableError>>()
                .combineTo(mutableListOf())

            assertEquals(
                expected = Ok(emptyList()),
                actual = result,
            )
        }
    }

    class CombineErrors {

        @Test
        fun returnsErrorsIfAllErr() {
            val result = combineErr(
                Err(IterableError.IterableError1),
                Err(IterableError.IterableError2),
            )

            assertEquals(
                expected = Err(listOf(IterableError.IterableError1, IterableError.IterableError2)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstOkIfAnyOk() {
            val result = combineErr(
                Err(IterableError.IterableError1),
                Ok(1),
                Err(IterableError.IterableError2),
                Ok(2),
            )

            assertEquals(
                expected = Ok(1),
                actual = result,
            )
        }

        @Test
        fun returnsErrEmptyListIfEmpty() {
            val result = emptyList<Result<Int, IterableError>>()
                .combineErr()

            assertEquals(
                expected = Err(emptyList()),
                actual = result,
            )
        }
    }

    class CombineErrTo {

        @Test
        fun returnsErrorsIfAllErr() {
            val result: Result<Int, Collection<IterableError>> = listOf(
                Err(IterableError.IterableError1),
                Err(IterableError.IterableError2),
            ).combineErrTo(mutableListOf())

            assertEquals(
                expected = Err(listOf(IterableError.IterableError1, IterableError.IterableError2)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstOkIfAnyOk() {
            val result: Result<Int, Collection<IterableError>> = listOf(
                Err(IterableError.IterableError1),
                Ok(1),
                Err(IterableError.IterableError2),
                Ok(2),
            ).combineErrTo(mutableListOf())

            assertEquals(
                expected = Ok(1),
                actual = result,
            )
        }

        @Test
        fun returnsErrEmptyCollectionIfEmpty() {
            val result: Result<Int, Collection<IterableError>> = emptyList<Result<Int, IterableError>>()
                .combineErrTo(mutableListOf())

            assertEquals(
                expected = Err(emptyList()),
                actual = result,
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
}
