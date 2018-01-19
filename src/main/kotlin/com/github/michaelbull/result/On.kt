package com.github.michaelbull.result

/**
 * Invokes a [callback] if this [Result] is [Ok].
 */
inline infix fun <V, E> Result<V, E>.onSuccess(callback: (V) -> Unit) = mapBoth(callback, {})

/**
 * Invokes a [callback] if this [Result] is [Err].
 */
inline infix fun <V, E> Result<V, E>.onFailure(callback: (E) -> Unit) = mapBoth({}, callback)
