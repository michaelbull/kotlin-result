package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Converts this [Result] containing a [Flow] into a [Flow] of [Results][Result].
 *
 * If this result [is ok][Result.isOk], each emission of the [Flow] is wrapped in [Ok].
 * If this result [is an error][Result.isErr], a single [Err] is emitted.
 *
 * This is useful for bridging a [Result] into a reactive pipeline. The caller first uses
 * `map` to transform the [value][Result.value] into a [Flow], then calls [toFlow]
 * to distribute the [Result] across each emission.
 *
 * Example:
 *
 * ```
 * fun findUser(id: Long): Result<User, FindUserError> { ... }
 *
 * fun observeOrders(user: User): Flow<Order> = flow {
 *     emit(Order(user.name, item = "Book", total = 10.0))
 *     emit(Order(user.name, item = "Album", total = 15.0))
 * }
 *
 * val orders: Flow<Result<Order, FindUserError>> = findUser(1L)
 *     .map(::observeOrders)
 *     .toFlow()
 *
 * // If findUser returns Ok(User("Alice")), the flow emits:
 * //   Ok(Order("Alice", "Book", 10.0))
 * //   Ok(Order("Alice", "Album", 15.0))
 * //
 * // If findUser returns Err(NotFound), the flow emits:
 * //   Err(NotFound)
 * ```
 */
public fun <V, E> Result<Flow<V>, E>.toFlow(): Flow<Result<V, E>> {
    return mapBoth(
        success = { it.map(::Ok) },
        failure = { flowOf(Err(it)) },
    )
}
