package com.mikebull94.result

inline fun <T, R, E> Iterable<T>.fold(
    initial: R,
    operation: (acc: R, T) -> Result<R, E>
): Result<R, E> {
    var accumulator = initial

    forEach { element ->
        val operationResult = operation(accumulator, element)

        when (operationResult) {
            is Ok -> {
                accumulator = operationResult.value
            }
            is Error -> return error(operationResult.error)
        }
    }

    return ok(accumulator)
}

inline fun <T, R, E> List<T>.foldRight(initial: R, operation: (T, acc: R) -> Result<R, E>): Result<R, E> {
    var accumulator = initial

    if (!isEmpty()) {
        val iterator = listIterator(size)
        while (iterator.hasPrevious()) {
            val operationResult = operation(iterator.previous(), accumulator)

            when (operationResult) {
                is Ok -> {
                    accumulator = operationResult.value
                }
                is Error -> return error(operationResult.error)
            }
        }
    }

    return ok(accumulator)
}

/**
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#combine)
 */
fun <V, E> combine(vararg results: Result<V, E>) = results.asIterable().combine()

/**
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#combine)
 */
fun <V, E> Iterable<Result<V, E>>.combine(): Result<List<V>, E> {
    return ok(map {
        when (it) {
            is Ok -> it.value
            is Error -> return error(it.error)
        }
    })
}

/**
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
fun <V, E> getAll(vararg results: Result<V, E>) = results.asIterable().getAll()

/**
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
fun <V, E> Iterable<Result<V, E>>.getAll(): List<V> {
    return filter { it is Ok }.map { (it as Ok).value }
}

/**
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
fun <V, E> getAllErrors(vararg results: Result<V, E>) = results.asIterable().getAllErrors()

/**
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
fun <V, E> Iterable<Result<V, E>>.getAllErrors(): List<E> {
    return filter { it is Error }.map { (it as Error).error }
}

/**
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
fun <V, E> partition(vararg results: Result<V, E>) = results.asIterable().partition()

/**
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
fun <V, E> Iterable<Result<V, E>>.partition(): Pair<List<V>, List<E>> {
    val values = mutableListOf<V>()
    val errors = mutableListOf<E>()

    forEach { result ->
        when (result) {
            is Ok -> values.add(result.value)
            is Error -> errors.add(result.error)
        }
    }

    return Pair(values, errors)
}
