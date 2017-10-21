package com.mikebull94.result

fun <V, E> Result<V, E>.onSuccess(callback: (V) -> Unit) = mapBoth(callback, {})
fun <V, E> Result<V, E>.onFailure(callback: (E) -> Unit) = mapBoth({}, callback)
