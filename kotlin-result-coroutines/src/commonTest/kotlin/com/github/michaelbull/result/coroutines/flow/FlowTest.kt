package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowTest {

    private sealed interface FlowError {
        data object FlowError1 : FlowError
        data object FlowError2 : FlowError
    }

    class FilterOk {

        @Test
        fun emitsOnlyOkValues() = runTest {
            val values = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
                Ok(3),
            ).filterOk().toList()

            assertEquals(
                expected = listOf(1, 2, 3),
                actual = values,
            )
        }

        @Test
        fun handlesNullableValues() = runTest {
            val values = flowOf(
                Ok(null),
                Err(FlowError.FlowError1),
                Ok(1),
            ).filterOk().toList()

            assertEquals(
                expected = listOf(null, 1),
                actual = values,
            )
        }
    }

    class FilterErr {

        @Test
        fun emitsOnlyErrValues() = runTest {
            val errors = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
            ).filterErr().toList()

            assertEquals(
                expected = listOf(FlowError.FlowError1, FlowError.FlowError2),
                actual = errors,
            )
        }

        @Test
        fun handlesNullableErrors() = runTest {
            val errors = flowOf(
                Ok(1),
                Err(null),
                Err("error"),
            ).filterErr().toList()

            assertEquals(
                expected = listOf(null, "error"),
                actual = errors,
            )
        }
    }

    class OnEachOk {

        @Test
        fun invokesActionForOkResults() = runTest {
            val collected = mutableListOf<Int>()

            flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
            ).onEachOk(collected::add).toList()

            assertEquals(
                expected = listOf(1, 2),
                actual = collected,
            )
        }

        @Test
        fun passesResultsThroughUnmodified() = runTest {
            val results = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
            ).onEachOk { }.toList()

            assertEquals(
                expected = listOf(Ok(1), Err(FlowError.FlowError1), Ok(2)),
                actual = results,
            )
        }
    }

    class OnEachErr {

        @Test
        fun invokesActionForErrResults() = runTest {
            val collected = mutableListOf<FlowError>()

            flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
            ).onEachErr(collected::add).toList()

            assertEquals(
                expected = listOf(FlowError.FlowError1, FlowError.FlowError2),
                actual = collected,
            )
        }

        @Test
        fun passesResultsThroughUnmodified() = runTest {
            val results = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
            ).onEachErr { }.toList()

            assertEquals(
                expected = listOf(Ok(1), Err(FlowError.FlowError1), Ok(2)),
                actual = results,
            )
        }
    }

    class AllOk {

        @Test
        fun returnsTrueIfAllOk() = runTest {
            val result = flowOf(
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
        fun returnsFalseIfAnyErr() = runTest {
            val result = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
            ).allOk()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class AllErr {

        @Test
        fun returnsTrueIfAllErr() = runTest {
            val result = flowOf(
                Err(FlowError.FlowError1),
                Err(FlowError.FlowError2),
            ).allErr()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAnyOk() = runTest {
            val result = flowOf(
                Err(FlowError.FlowError1),
                Ok(1),
                Err(FlowError.FlowError2),
            ).allErr()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class AnyOk {

        @Test
        fun returnsTrueIfAnyOk() = runTest {
            val result = flowOf(
                Err(FlowError.FlowError1),
                Ok(1),
                Err(FlowError.FlowError2),
            ).anyOk()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAllErr() = runTest {
            val result = flowOf(
                Err(FlowError.FlowError1),
                Err(FlowError.FlowError2),
            ).anyOk()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class AnyErr {

        @Test
        fun returnsTrueIfAnyErr() = runTest {
            val result = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
            ).anyErr()

            assertEquals(
                expected = true,
                actual = result,
            )
        }

        @Test
        fun returnsFalseIfAllOk() = runTest {
            val result = flowOf(
                Ok(1),
                Ok(2),
                Ok(3),
            ).anyErr()

            assertEquals(
                expected = false,
                actual = result,
            )
        }
    }

    class CountOk {

        @Test
        fun countsOkElements() = runTest {
            val result = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
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
        fun countsErrElements() = runTest {
            val result = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
                Ok(3),
            ).countErr()

            assertEquals(
                expected = 2,
                actual = result,
            )
        }
    }

    class Partition {

        @Test
        fun splitsIntoValuesAndErrors() = runTest {
            val (values, errors) = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
                Ok(3),
            ).partition()

            assertEquals(
                expected = Pair(
                    listOf(1, 2, 3),
                    listOf(FlowError.FlowError1, FlowError.FlowError2),
                ),
                actual = Pair(values, errors),
            )
        }

        @Test
        fun returnsEmptyListsIfFlowIsEmpty() = runTest {
            val (values, errors) = flowOf<Result<Int, FlowError>>()
                .partition()

            assertEquals(
                expected = Pair(emptyList<Int>(), emptyList<FlowError>()),
                actual = Pair(values, errors),
            )
        }
    }

    class Combine {

        @Test
        fun returnsOkListIfAllOk() = runTest {
            val result = flowOf(
                Ok(1),
                Ok(2),
                Ok(3),
            ).combine()

            assertEquals(
                expected = Ok(listOf(1, 2, 3)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstErrIfAnyErr() = runTest {
            val result: Result<List<Int>, FlowError> = flowOf(
                Ok(1),
                Err(FlowError.FlowError1),
                Ok(2),
                Err(FlowError.FlowError2),
            ).combine()

            assertEquals(
                expected = Err(FlowError.FlowError1),
                actual = result,
            )
        }

        @Test
        fun returnsOkEmptyListIfFlowIsEmpty() = runTest {
            val result = flowOf<Result<Int, FlowError>>()
                .combine()

            assertEquals(
                expected = Ok(emptyList()),
                actual = result,
            )
        }
    }

    class CombineErrors {

        @Test
        fun returnsErrListIfAllErr() = runTest {
            val result = flowOf(
                Err(FlowError.FlowError1),
                Err(FlowError.FlowError2),
            ).combineErr()

            assertEquals(
                expected = Err(listOf(FlowError.FlowError1, FlowError.FlowError2)),
                actual = result,
            )
        }

        @Test
        fun returnsFirstOkIfAnyOk() = runTest {
            val result: Result<Int, List<FlowError>> = flowOf(
                Err(FlowError.FlowError1),
                Ok(1),
                Err(FlowError.FlowError2),
                Ok(2),
            ).combineErr()

            assertEquals(
                expected = Ok(1),
                actual = result,
            )
        }

        @Test
        fun returnsErrEmptyListIfFlowIsEmpty() = runTest {
            val result = flowOf<Result<Int, FlowError>>()
                .combineErr()

            assertEquals(
                expected = Err(emptyList()),
                actual = result,
            )
        }
    }
}
