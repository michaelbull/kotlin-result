package com.github.michaelbull.result

/**
 * Invokes a [callback] if this [Result] is [Ok].
 */
infix inline fun <V, E> Result<V, E>.onSuccess(callback: (V) -> Unit) = mapBoth(callback, {})

/**
 * Invokes a [callback] if this [Result] is [Err].
 */
infix inline fun <V, E> Result<V, E>.onFailure(callback: (E) -> Unit) = mapBoth({}, callback)
