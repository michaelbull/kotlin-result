package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FactoryTest {

    class ToFlow {

        @Test
        fun okResultProducesMappedFlow() = runTest {
            val result: Result<Int, String> = Ok(1)
            val results = result
                .map { flowOf(it * 2, it * 3) }
                .toFlow()
                .toList()

            assertEquals(
                expected = listOf(Ok(2), Ok(3)),
                actual = results,
            )
        }

        @Test
        fun errResultProducesSingleErrFlow() = runTest {
            val result: Result<Int, String> = Err("error")
            val results = result
                .map { flowOf(it * 2, it * 3) }
                .toFlow()
                .toList()

            assertEquals(
                expected = listOf(Err("error")),
                actual = results,
            )
        }
    }
}
