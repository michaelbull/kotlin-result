package com.github.michaelbull.result.bind

import com.github.michaelbull.result.Result

interface ResultBinding<E> {
    fun <V> Result<V, E>.bind(): V
}
