package com.github.michaelbull.result.coroutines.binding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.CoroutineBindingScope
import com.github.michaelbull.result.coroutines.coroutineBinding

@Deprecated(
    message = "Use coroutineBinding instead",
    replaceWith = ReplaceWith(
        expression = "coroutineBinding(block)",
        imports = ["com.github.michaelbull.result.coroutines.coroutineBinding"]
    )
)
public suspend inline fun <V, E> binding(crossinline block: suspend CoroutineBindingScope<E>.() -> V): Result<V, E> {
    return coroutineBinding(block)
}

@Deprecated(
    message = "Use CoroutineBindingScope instead",
    replaceWith = ReplaceWith("CoroutineBindingScope<E>")
)
public typealias SuspendableResultBinding<E> = CoroutineBindingScope<E>
