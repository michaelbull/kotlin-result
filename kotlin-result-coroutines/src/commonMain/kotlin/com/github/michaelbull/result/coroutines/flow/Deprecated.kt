package com.github.michaelbull.result.coroutines.flow

import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

@Deprecated("Use tryCollect instead.", ReplaceWith("tryCollect(action)"))
public suspend inline fun <V, E> Flow<V>.tryForEach(
    crossinline action: suspend (V) -> Result<*, E>,
): Result<Unit, E> {
    return tryCollect(action)
}

@Deprecated("Use tryFirstOrNull instead.", ReplaceWith("tryFirstOrNull(predicate)"))
public suspend inline fun <T, E> Flow<T>.tryFind(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<T, E>? {
    return tryFirstOrNull(predicate)
}

@Deprecated("Use tryLastOrNull instead.", ReplaceWith("tryLastOrNull(predicate)"))
public suspend inline fun <T, E> Flow<T>.tryFindLast(
    crossinline predicate: suspend (T) -> Result<Boolean, E>,
): Result<T, E>? {
    return tryLastOrNull(predicate)
}
