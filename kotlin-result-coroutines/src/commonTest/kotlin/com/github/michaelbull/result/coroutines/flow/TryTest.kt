package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TryTest {

    private sealed interface TryError {
        data object TryError1 : TryError
        data object TryError2 : TryError
    }

    class TryFilter {

        @Test
        fun returnsFilteredElementsIfAllOk() = runTest {
            val result = flowOf(1, 2, 3, 4, 5).tryFilter { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(2, 4)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() = runTest {
            val result: Result<List<Int>, String> = flowOf(1, 2, 3, 4).tryFilter { element ->
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

    class TryFilterNot {

        @Test
        fun returnsNonMatchingElementsIfAllOk() = runTest {
            val result = flowOf(1, 2, 3, 4, 5).tryFilterNot { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(listOf(1, 3, 5)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() = runTest {
            val result: Result<List<Int>, String> = flowOf(1, 2, 3).tryFilterNot { element ->
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

    class TryMap {

        @Test
        fun returnsOkListIfAllTransformsSucceed() = runTest {
            val result = flowOf(1, 2, 3)
                .tryMap { Ok(it * 2) }

            assertEquals(
                expected = Ok(listOf(2, 4, 6)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfAnyTransformFails() = runTest {
            val result: Result<List<Int>, TryError> = flowOf(1, 2, 3)
                .tryMap { element ->
                    if (element == 2) {
                        Err(TryError.TryError1)
                    } else {
                        Ok(element * 2)
                    }
                }

            assertEquals(
                expected = Err(TryError.TryError1),
                actual = result,
            )
        }
    }

    class TryMapNotNull {

        @Test
        fun returnsNonNullTransformedValuesIfAllOk() = runTest {
            val result = flowOf(1, 2, 3).tryMapNotNull { element ->
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
        fun returnsFirstErrIfTransformFails() = runTest {
            val result: Result<List<Int>, String> = flowOf(1, 2, 3).tryMapNotNull { element ->
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
        fun returnsFlattenedListIfAllOk() = runTest {
            val result = flowOf(1, 2, 3).tryFlatMap { element ->
                Ok(listOf(element, element * 10))
            }

            assertEquals(
                expected = Ok(listOf(1, 10, 2, 20, 3, 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() = runTest {
            val result: Result<List<Int>, String> = flowOf(1, 2, 3).tryFlatMap { element ->
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

    class TryForEach {

        @Test
        fun returnsOkIfAllActionsSucceed() = runTest {
            val collected = mutableListOf<Int>()

            val result = flowOf(1, 2, 3).tryForEach { element ->
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
        fun returnsFirstErrIfActionFails() = runTest {
            val collected = mutableListOf<Int>()

            val result: Result<Unit, String> = flowOf(1, 2, 3).tryForEach { element ->
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

    class TryReduce {

        @Test
        fun returnsReducedValueIfOk() = runTest {
            val result = flowOf(1, 2, 3, 4).tryReduce { acc, element ->
                Ok(acc + element)
            }

            assertEquals(
                expected = Ok(10),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfOperationFails() = runTest {
            val result = flowOf(1, 2, 3, 4).tryReduce { acc, element ->
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
        fun returnsNullIfEmpty() = runTest {
            val result = flowOf<Int>().tryReduce { acc, element ->
                Ok(acc + element)
            }

            assertEquals(
                expected = null,
                actual = result,
            )
        }
    }

    class TryFold {

        @Test
        fun returnsAccumulatedValueIfAllOk() = runTest {
            val result = flowOf(1, 2, 3)
                .tryFold(0) { acc, element -> Ok(acc + element) }

            assertEquals(
                expected = Ok(6),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrorIfAnyErr() = runTest {
            val result: Result<Int, TryError> = flowOf(1, 2, 3)
                .tryFold(0) { acc, element ->
                    if (element == 2) {
                        Err(TryError.TryError1)
                    } else {
                        Ok(acc + element)
                    }
                }

            assertEquals(
                expected = Err(TryError.TryError1),
                actual = result,
            )
        }
    }

    class TryFind {

        @Test
        fun returnsMatchingElement() = runTest {
            val result = flowOf(1, 2, 3, 4).tryFind { element ->
                Ok(element == 3)
            }

            assertEquals(
                expected = Ok(3),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() = runTest {
            val result = flowOf(1, 2, 3, 4).tryFind { element ->
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
        fun returnsNullIfNoMatch() = runTest {
            val result = flowOf(1, 2, 3).tryFind { element ->
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
        fun returnsLastMatchingElement() = runTest {
            val result = flowOf(1, 2, 3, 4).tryFindLast { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(4),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() = runTest {
            val result = flowOf(1, 2, 3, 4).tryFindLast { element ->
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
        fun returnsNullIfNoMatch() = runTest {
            val result = flowOf(1, 2, 3).tryFindLast { element ->
                Ok(element == 99)
            }

            assertEquals(
                expected = null,
                actual = result,
            )
        }
    }

    class TryAssociate {

        @Test
        fun returnsMapIfAllOk() = runTest {
            val result = flowOf(1, 2, 3).tryAssociate { element ->
                Ok(element.toString() to element * 10)
            }

            assertEquals(
                expected = Ok(mapOf("1" to 10, "2" to 20, "3" to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfTransformFails() = runTest {
            val result: Result<Map<String, Int>, String> = flowOf(1, 2, 3).tryAssociate { element ->
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
        fun returnsMapWithKeysIfAllOk() = runTest {
            val result = flowOf(1, 2, 3).tryAssociateBy { element ->
                Ok(element.toString())
            }

            assertEquals(
                expected = Ok(mapOf("1" to 1, "2" to 2, "3" to 3)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() = runTest {
            val result: Result<Map<String, Int>, String> = flowOf(1, 2, 3).tryAssociateBy { element ->
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
        fun returnsMapIfAllOk() = runTest {
            val result = flowOf(1, 2, 3).tryAssociateBy(
                keySelector = { Ok(it.toString()) },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Ok(mapOf("1" to 10, "2" to 20, "3" to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() = runTest {
            val result: Result<Map<String, Int>, String> = flowOf(1, 2, 3).tryAssociateBy(
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
        fun returnsFirstErrIfValueTransformFails() = runTest {
            val result: Result<Map<String, Int>, String> = flowOf(1, 2, 3).tryAssociateBy(
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

    class TryAssociateWith {

        @Test
        fun returnsMapIfAllOk() = runTest {
            val result = flowOf(1, 2, 3).tryAssociateWith { element ->
                Ok(element * 10)
            }

            assertEquals(
                expected = Ok(mapOf(1 to 10, 2 to 20, 3 to 30)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfValueSelectorFails() = runTest {
            val result: Result<Map<Int, Int>, String> = flowOf(1, 2, 3).tryAssociateWith { element ->
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

    class TryGroupByKeyOnly {

        @Test
        fun returnsGroupedMapIfAllOk() = runTest {
            val result = flowOf(1, 2, 3, 4, 5).tryGroupBy { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(mapOf(false to listOf(1, 3, 5), true to listOf(2, 4))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() = runTest {
            val result: Result<Map<Boolean, List<Int>>, String> = flowOf(1, 2, 3).tryGroupBy { element ->
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
        fun returnsGroupedTransformedMapIfAllOk() = runTest {
            val result = flowOf(1, 2, 3, 4, 5).tryGroupBy(
                keySelector = { Ok(it % 2 == 0) },
                valueTransform = { Ok(it * 10) },
            )

            assertEquals(
                expected = Ok(mapOf(false to listOf(10, 30, 50), true to listOf(20, 40))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfKeySelectorFails() = runTest {
            val result: Result<Map<Boolean, List<Int>>, String> = flowOf(1, 2, 3).tryGroupBy(
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
        fun returnsFirstErrIfValueTransformFails() = runTest {
            val result: Result<Map<Boolean, List<Int>>, String> = flowOf(1, 2, 3).tryGroupBy(
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

    class TryPartition {

        @Test
        fun returnsPartitionedPairIfAllOk() = runTest {
            val result = flowOf(1, 2, 3, 4, 5).tryPartition { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(Pair(listOf(2, 4), listOf(1, 3, 5))),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfPredicateFails() = runTest {
            val result: Result<Pair<List<Int>, List<Int>>, String> = flowOf(1, 2, 3, 4).tryPartition { element ->
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
        fun returnsEmptyPairIfEmpty() = runTest {
            val result = flowOf<Int>().tryPartition { element ->
                Ok(element % 2 == 0)
            }

            assertEquals(
                expected = Ok(Pair(emptyList(), emptyList())),
                actual = result,
            )
        }
    }
}
