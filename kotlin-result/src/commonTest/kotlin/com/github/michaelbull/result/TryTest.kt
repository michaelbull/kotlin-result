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

    class TryFilter {

        @Test
        fun returnsFilteredElementsIfAllOk() {
            val result = listOf(1, 2, 3, 4, 5).tryFilter { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(2, 4)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result: Result<List<Int>, String> = listOf(1, 2, 3, 4).tryFilter { element ->
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
    }

    class TryFilterTo {

        @Test
        fun appendsFilteredElementsIfAllOk() {
            val destination = mutableListOf(0)

            val result = listOf(1, 2, 3, 4, 5).tryFilterTo(destination) { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(0, 2, 4)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val destination = mutableListOf(0)

            val result: Result<MutableList<Int>, String> = listOf(1, 2, 3).tryFilterTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(true)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFilterNot {

        @Test
        fun returnsNonMatchingElementsIfAllOk() {
            val result = listOf(1, 2, 3, 4, 5).tryFilterNot { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(1, 3, 5)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result: Result<List<Int>, String> = listOf(1, 2, 3).tryFilterNot { element ->
                if (element == 2) {
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
    }

    class TryFilterNotTo {

        @Test
        fun appendsNonMatchingElementsIfAllOk() {
            val destination = mutableListOf(0)

            val result = listOf(1, 2, 3, 4, 5).tryFilterNotTo(destination) { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(0, 1, 3, 5)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val destination = mutableListOf(0)

            val result: Result<MutableList<Int>, String> = listOf(1, 2, 3).tryFilterNotTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(false)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFilterIndexed {

        @Test
        fun returnsFilteredElementsByIndexIfAllOk() {
            val result = listOf(10, 20, 30, 40).tryFilterIndexed { index, _ ->
                Ok(index % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(10, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result: Result<List<Int>, String> = listOf(10, 20, 30).tryFilterIndexed { index, _ ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(true)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFilterIndexedTo {

        @Test
        fun appendsFilteredElementsByIndexIfAllOk() {
            val destination = mutableListOf(0)

            val result = listOf(10, 20, 30, 40).tryFilterIndexedTo(destination) { index, _ ->
                Ok(index % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(0, 10, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val destination = mutableListOf(0)

            val result: Result<MutableList<Int>, String> = listOf(10, 20, 30).tryFilterIndexedTo(destination) { index, _ ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(true)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryAssociate {

        @Test
        fun returnsMapIfAllOk() {
            val result = listOf(1, 2, 3).tryAssociate { element ->
                Ok(element.toString() to element * 10)
            }

            assertEquals(
                expected = Ok(mapOf("1" to 10, "2" to 20, "3" to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<Map<String, Int>, String> = listOf(1, 2, 3).tryAssociate { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element.toString() to element * 10)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryAssociateTo {

        @Test
        fun appendsPairsToDestinationIfAllOk() {
            val destination = mutableMapOf("0" to 0)

            val result = listOf(1, 2, 3).tryAssociateTo(destination) { element ->
                Ok(element.toString() to element * 10)
            }

            assertEquals(
                expected = Ok(mapOf("0" to 0, "1" to 10, "2" to 20, "3" to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableMapOf("0" to 0)

            val result: Result<MutableMap<String, Int>, String> = listOf(1, 2, 3).tryAssociateTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element.toString() to element * 10)
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryAssociateByKeyOnly {

        @Test
        fun returnsMapWithKeysIfAllOk() {
            val result = listOf(1, 2, 3).tryAssociateBy { element ->
                Ok(element.toString())
            }

            assertEquals(
                expected = Ok(mapOf("1" to 1, "2" to 2, "3" to 3)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val result: Result<Map<String, Int>, String> = listOf(1, 2, 3).tryAssociateBy { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element.toString())
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryAssociateByKeyAndValue {

        @Test
        fun returnsMapIfAllOk() {
            val result = listOf(1, 2, 3).tryAssociateBy(
                keySelector = { Ok(it.toString()) },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Ok(mapOf("1" to 10, "2" to 20, "3" to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val result: Result<Map<String, Int>, String> = listOf(1, 2, 3).tryAssociateBy(
                keySelector = { element ->
                    if (element == 2) {
                        Err("bad key")
                    } else {
                        Ok(element.toString())
                    }
                },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Err("bad key"),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfValueTransformFails() {
            val result: Result<Map<String, Int>, String> = listOf(1, 2, 3).tryAssociateBy(
                keySelector = { Ok(it.toString()) },
                valueTransform = { element ->
                    if (element == 2) {
                        Err("bad value")
                    } else {
                        Ok(element * 10)
                    }
                },
            )

            assertEquals(
                expected = Err("bad value"),
                actual = result,
            )
        }
    }

    class TryAssociateByToKeyOnly {

        @Test
        fun appendsToDestinationIfAllOk() {
            val destination = mutableMapOf("0" to 0)

            val result = listOf(1, 2, 3).tryAssociateByTo(destination) { element ->
                Ok(element.toString())
            }

            assertEquals(
                expected = Ok(mapOf("0" to 0, "1" to 1, "2" to 2, "3" to 3)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val destination = mutableMapOf("0" to 0)

            val result: Result<MutableMap<String, Int>, String> = listOf(1, 2, 3).tryAssociateByTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(element.toString())
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryAssociateByToKeyAndValue {

        @Test
        fun appendsToDestinationIfAllOk() {
            val destination = mutableMapOf("0" to 0)

            val result = listOf(1, 2, 3).tryAssociateByTo(
                destination = destination,
                keySelector = { Ok(it.toString()) },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Ok(mapOf("0" to 0, "1" to 10, "2" to 20, "3" to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val destination = mutableMapOf("0" to 0)

            val result: Result<MutableMap<String, Int>, String> = listOf(1, 2, 3).tryAssociateByTo(
                destination = destination,
                keySelector = { element ->
                    if (element == 2) {
                        Err("bad")
                    } else {
                        Ok(element.toString())
                    }
                },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryAssociateWith {

        @Test
        fun returnsMapIfAllOk() {
            val result = listOf(1, 2, 3).tryAssociateWith { element ->
                Ok(element * 10)
            }

            assertEquals(
                expected = Ok(mapOf(1 to 10, 2 to 20, 3 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfValueSelectorFails() {
            val result: Result<Map<Int, Int>, String> = listOf(1, 2, 3).tryAssociateWith { element ->
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

    class TryAssociateWithTo {

        @Test
        fun appendsToDestinationIfAllOk() {
            val destination = mutableMapOf(0 to 0)

            val result = listOf(1, 2, 3).tryAssociateWithTo(destination) { element ->
                Ok(element * 10)
            }

            assertEquals(
                expected = Ok(mapOf(0 to 0, 1 to 10, 2 to 20, 3 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfValueSelectorFails() {
            val destination = mutableMapOf(0 to 0)

            val result: Result<MutableMap<Int, Int>, String> = listOf(1, 2, 3).tryAssociateWithTo(destination) { element ->
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

    class TryFlatMap {

        @Test
        fun returnsFlattenedListIfAllOk() {
            val result = listOf(1, 2, 3).tryFlatMap { element ->
                Ok(listOf(element, element * 10))
            }

            assertEquals(
                expected = Ok(listOf(1, 10, 2, 20, 3, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<List<Int>, String> = listOf(1, 2, 3).tryFlatMap { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(listOf(element, element * 10))
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFlatMapTo {

        @Test
        fun appendsFlattenedElementsIfAllOk() {
            val destination = mutableListOf(0)

            val result = listOf(1, 2, 3).tryFlatMapTo(destination) { element ->
                Ok(listOf(element, element * 10))
            }

            assertEquals(
                expected = Ok(listOf(0, 1, 10, 2, 20, 3, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableListOf(0)

            val result: Result<MutableList<Int>, String> = listOf(1, 2, 3).tryFlatMapTo(destination) { element ->
                if (element == 2) {
                    Err("bad")
                } else {
                    Ok(listOf(element, element * 10))
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFlatMapIndexed {

        @Test
        fun returnsFlattenedListWithIndicesIfAllOk() {
            val result = listOf(10, 20, 30).tryFlatMapIndexed { index, value ->
                Ok(listOf(index, value))
            }

            assertEquals(
                expected = Ok(listOf(0, 10, 1, 20, 2, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val result: Result<List<Int>, String> = listOf(10, 20, 30).tryFlatMapIndexed { index, value ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(listOf(index, value))
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryFlatMapIndexedTo {

        @Test
        fun appendsFlattenedElementsWithIndicesIfAllOk() {
            val destination = mutableListOf(-1)

            val result = listOf(10, 20, 30).tryFlatMapIndexedTo(destination) { index, value ->
                Ok(listOf(index, value))
            }

            assertEquals(
                expected = Ok(listOf(-1, 0, 10, 1, 20, 2, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() {
            val destination = mutableListOf(-1)

            val result: Result<MutableList<Int>, String> = listOf(10, 20, 30).tryFlatMapIndexedTo(destination) { index, value ->
                if (index == 1) {
                    Err("bad")
                } else {
                    Ok(listOf(index, value))
                }
            }

            assertEquals(
                expected = Err("bad"),
                actual = result,
            )
        }
    }

    class TryGroupByKeyOnly {

        @Test
        fun returnsGroupedMapIfAllOk() {
            val result = listOf(1, 2, 3, 4, 5).tryGroupBy { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(mapOf(false to listOf(1, 3, 5), true to listOf(2, 4))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val result: Result<Map<Boolean, List<Int>>, String> = listOf(1, 2, 3).tryGroupBy { element ->
                if (element == 2) {
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
    }

    class TryGroupByKeyAndValue {

        @Test
        fun returnsGroupedTransformedMapIfAllOk() {
            val result = listOf(1, 2, 3, 4, 5).tryGroupBy(
                keySelector = { Ok(it % 2 == 0) },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Ok(mapOf(false to listOf(10, 30, 50), true to listOf(20, 40))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val result: Result<Map<Boolean, List<Int>>, String> = listOf(1, 2, 3).tryGroupBy(
                keySelector = { element ->
                    if (element == 2) {
                        Err("bad key")
                    } else {
                        Ok(element % 2 == 0)
                    }
                },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Err("bad key"),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfValueTransformFails() {
            val result: Result<Map<Boolean, List<Int>>, String> = listOf(1, 2, 3).tryGroupBy(
                keySelector = { Ok(it % 2 == 0) },
                valueTransform = { element ->
                    if (element == 2) {
                        Err("bad value")
                    } else {
                        Ok(element * 10)
                    }
                },
            )

            assertEquals(
                expected = Err("bad value"),
                actual = result,
            )
        }
    }

    class TryGroupByToKeyOnly {

        @Test
        fun appendsToDestinationIfAllOk() {
            val destination = LinkedHashMap<Boolean, MutableList<Int>>()

            val result = listOf(1, 2, 3, 4, 5).tryGroupByTo(destination) { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(mapOf(false to listOf(1, 3, 5), true to listOf(2, 4))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val destination = LinkedHashMap<Boolean, MutableList<Int>>()

            val result: Result<LinkedHashMap<Boolean, MutableList<Int>>, String> = listOf(1, 2, 3).tryGroupByTo(destination) { element ->
                if (element == 2) {
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
    }

    class TryGroupByToKeyAndValue {

        @Test
        fun appendsToDestinationIfAllOk() {
            val destination = LinkedHashMap<Boolean, MutableList<Int>>()

            val result = listOf(1, 2, 3, 4, 5).tryGroupByTo(
                destination = destination,
                keySelector = { Ok(it % 2 == 0) },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Ok(mapOf(false to listOf(10, 30, 50), true to listOf(20, 40))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() {
            val destination = LinkedHashMap<Boolean, MutableList<Int>>()

            val result: Result<LinkedHashMap<Boolean, MutableList<Int>>, String> = listOf(1, 2, 3).tryGroupByTo(
                destination = destination,
                keySelector = { element ->
                    if (element == 2) {
                        Err("bad")
                    } else {
                        Ok(element % 2 == 0)
                    }
                },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Err("bad"),
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

    class TryPartition {

        @Test
        fun returnsPartitionedPairIfAllOk() {
            val result = listOf(1, 2, 3, 4, 5).tryPartition { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(Pair(listOf(2, 4), listOf(1, 3, 5))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result: Result<Pair<List<Int>, List<Int>>, String> = listOf(1, 2, 3, 4).tryPartition { element ->
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
        fun returnsEmptyPairIfEmpty() {
            val result = emptyList<Int>().tryPartition { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(Pair(emptyList(), emptyList())),
                actual = result,
            )
        }
    }

    class TryPartitionTo {

        @Test
        fun appendsMatchingElementsToFirstDestination() {
            val first = mutableListOf(0)
            val second = mutableListOf<Int>()

            listOf(1, 2, 3, 4, 5).tryPartitionTo(first, second) { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = listOf(0, 2, 4),
                actual = first,
            )
        }

        @Test
        fun appendsNonMatchingElementsToSecondDestination() {
            val first = mutableListOf<Int>()
            val second = mutableListOf(0)

            listOf(1, 2, 3, 4, 5).tryPartitionTo(first, second) { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = listOf(0, 1, 3, 5),
                actual = second,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() {
            val result: Result<Pair<Collection<Int>, Collection<Int>>, String> =
                listOf(1, 2, 3, 4).tryPartitionTo(mutableListOf(), mutableListOf()) { element ->
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
    }
}
