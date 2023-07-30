package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Apply a [transformation][transform] to ten [Results][Result], if both [Results][Result] are [Ok].
 * If not, the all arguments which are [Err] will propagate through.
 */
public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, E, Z> zipOrAccumulate(
    producer1: () -> Result<T1, E>,
    producer2: () -> Result<T2, E>,
    producer3: () -> Result<T3, E>,
    producer4: () -> Result<T4, E>,
    producer5: () -> Result<T5, E>,
    producer6: () -> Result<T6, E>,
    producer7: () -> Result<T7, E>,
    producer8: () -> Result<T8, E>,
    producer9: () -> Result<T9, E>,
    producer10: () -> Result<T10, E>,
    transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> Z,
): Result<Z, Collection<E>> {
    contract {
        callsInPlace(producer1, InvocationKind.EXACTLY_ONCE)
        callsInPlace(producer2, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer3, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer4, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer5, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer6, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer7, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer8, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer9, InvocationKind.AT_MOST_ONCE)
        callsInPlace(producer10, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    val result1 = producer1()
    val result2 = producer2()
    val result3 = producer3()
    val result4 = producer4()
    val result5 = producer5()
    val result6 = producer6()
    val result7 = producer7()
    val result8 = producer8()
    val result9 = producer9()
    val result10 = producer10()

    return if (
        result1 is Ok &&
        result2 is Ok &&
        result3 is Ok &&
        result4 is Ok &&
        result5 is Ok &&
        result6 is Ok &&
        result7 is Ok &&
        result8 is Ok &&
        result9 is Ok &&
        result10 is Ok
    ) {
        Ok(
            transform(
                result1.value,
                result2.value,
                result3.value,
                result4.value,
                result5.value,
                result6.value,
                result7.value,
                result8.value,
                result9.value,
                result10.value,
            ),
        )
    } else {
        Err(
            listOf(
                result1,
                result2,
                result3,
                result4,
                result5,
                result6,
                result7,
                result8,
                result9,
                result10,
            ).mapNotNull { (it as? Err)?.error },
        )
    }
}
