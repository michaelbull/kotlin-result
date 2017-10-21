package com.mikebull94.result

fun <V> ok(value: V) = Ok<V, Nothing>(value)
fun <E> error(error: E) = Error<Nothing, E>(error)
