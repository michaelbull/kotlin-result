package com.github.michaelbull.result

/**
 * Calls a [callback] if the [Result] is [Ok].
 * @param callback The function to call.
 */
fun <V, E> Result<V, E>.onSuccess(callback: (V) -> Unit) = mapBoth(callback, {})

/**
 * Calls a [callback] if the [Result] is [Err].
 * @param callback The function to call.
 */
fun <V, E> Result<V, E>.onFailure(callback: (E) -> Unit) = mapBoth({}, callback)
