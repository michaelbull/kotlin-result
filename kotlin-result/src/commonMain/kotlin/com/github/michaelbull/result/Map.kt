package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function to the [value][Result.value] if this result [is ok][Result.isOk], or returning [this].
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
public inline infix fun <V, E, U> Result<V, E>.map(transform: (V) -> U): Result<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> Ok(transform(value))
        else -> this.asErr()
    }
}

/**
 * Maps this [Result<V, Throwable>][Result] to [Result<U, Throwable>][Result] by either applying
 * the [transform] function to the [value][Result.value] if this result [is ok][Result.isOk], or
 * returning [this].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates
 * it as an [Err].
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
public inline infix fun <V, U> Result<V, Throwable>.mapCatching(transform: (V) -> U): Result<U, Throwable> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> runCatching { transform(value) }
        else -> this.asErr()
    }
}

/**
 * Transposes this [Result<V?, E>][Result] to [Result<V, E>][Result].
 *
 * Returns null if this [Result] is [Ok] and the [value][Ok.value] is `null`, otherwise this [Result].
 *
 * - Rust: [Result.transpose][https://doc.rust-lang.org/std/result/enum.Result.html#method.transpose]
 */
public inline fun <V, E> Result<V?, E>.transpose(): Result<V, E>? {
    return when {
        isOk && value == null -> null
        isOk && value != null -> this.asOk()
        else -> this.asErr()
    }
}

/**
 * Maps this [Result<Result<V, E>, E>][Result] to [Result<V, E>][Result].
 *
 * - Rust: [Result.flatten](https://doc.rust-lang.org/std/result/enum.Result.html#method.flatten)
 */
public fun <V, E> Result<Result<V, E>, E>.flatten(): Result<V, E> {
    return when {
        isOk -> value
        else -> this.asErr()
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by either applying the [transform]
 * function if this result [is ok][Result.isOk], or returning [this].
 *
 * This is functionally equivalent to [andThen].
 *
 * - Scala: [Either.flatMap](http://www.scala-lang.org/api/2.12.0/scala/util/Either.html#flatMap[AA>:A,Y](f:B=>scala.util.Either[AA,Y]):scala.util.Either[AA,Y])
 */
public inline infix fun <V, E, U> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return andThen(transform)
}

/**
 * Maps this [Result<V, E>][Result] to [U] by applying either the [success] function if this
 * result [is ok][Result.isOk], or the [failure] function if this result
 * [is an error][Result.isErr].
 *
 * Unlike [mapEither], [success] and [failure] must both return [U].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> Result<V, E>.mapBoth(
    success: (V) -> U,
    failure: (E) -> U,
): U {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> success(value)
        else -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][Result] to [U] by applying either the [success] function if this
 * result [is ok][Result.isOk], or the [failure] function if this result
 * [is an error][Result.isErr].
 *
 * Unlike [mapEither], [success] and [failure] must both return [U].
 *
 * This is functionally equivalent to [mapBoth].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> Result<V, E>.fold(
    success: (V) -> U,
    failure: (E) -> U,
): U {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return mapBoth(success, failure)
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, E>][Result] by applying either the [success]
 * function if this result [is ok][Result.isOk], or the [failure] function if this result
 * [is an error][Result.isErr].
 *
 * Unlike [mapEither], [success] and [failure] must both return [U].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> Result<V, E>.flatMapBoth(
    success: (V) -> Result<U, E>,
    failure: (E) -> Result<U, E>,
): Result<U, E> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> success(value)
        else -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, F>][Result] by applying either the [success]
 * function if this result [is ok][Result.isOk], or the [failure] function if this result
 * [is an error][Result.isErr].
 *
 * Unlike [mapBoth], [success] and [failure] may either return [U] or [F] respectively.
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
public inline fun <V, E, U, F> Result<V, E>.mapEither(
    success: (V) -> U,
    failure: (E) -> F,
): Result<U, F> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> Ok(success(value))
        else -> Err(failure(error))
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<U, F>][Result] by applying either the [success]
 * function if this result [is ok][Result.isOk], or the [failure] function if this result
 * [is an error][Result.isErr].
 *
 * Unlike [mapBoth], [success] and [failure] may either return [U] or [F] respectively.
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
public inline fun <V, E, U, F> Result<V, E>.flatMapEither(
    success: (V) -> Result<U, F>,
    failure: (E) -> Result<U, F>,
): Result<U, F> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> success(value)
        else -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][Result] to [Result<V, F>][Result] by either applying the [transform]
 * function to the [error][Result.error] if this result [is an error][Result.isErr], or returning
 * [this].
 *
 * - Elm: [Result.mapError](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#mapError)
 * - Haskell: [Data.Bifunctor.right](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:second)
 * - Rust: [Result.map_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_err)
 */
public inline infix fun <V, E, F> Result<V, E>.mapError(transform: (E) -> F): Result<V, F> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr -> Err(transform(error))
        else -> this.asOk()
    }
}

/**
 * Maps this [Result<V, E>][Result] to [U] by either applying the [transform] function to the
 * [value][Result.value] if this result [is ok][Result.isOk], or returning the [default] if this
 * result [is an error][Result.isErr].
 *
 * - Rust: [Result.map_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_or)
 */
public inline fun <V, E, U> Result<V, E>.mapOr(
    default: U,
    transform: (V) -> U,
): U {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> transform(value)
        else -> default
    }
}

/**
 * Maps this [Result<V, E>][Result] to [U] by applying either the [transform] function if this
 * result [is ok][Result.isOk], or the [default] function if this result
 * [is an error][Result.isErr]. Both of these functions must return the same type ([U]).
 *
 * - Rust: [Result.map_or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_or_else)
 */
public inline fun <V, E, U> Result<V, E>.mapOrElse(
    default: (E) -> U,
    transform: (V) -> U,
): U {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> transform(value)
        else -> default(error)
    }
}

/**
 * Returns a [Result<List<U>, E>][Result] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails.
 */
public inline infix fun <V, E, U> Result<Iterable<V>, E>.mapAll(transform: (V) -> Result<U, E>): Result<List<U>, E> {
    return map { iterable ->
        iterable.map { element ->
            val transformed = transform(element)

            when {
                transformed.isOk -> transformed.value
                else -> return transformed.asErr()
            }
        }
    }
}

/**
 * Returns the [transformation][transform] of the [value][Result.value] if this result
 * [is ok][Result.isOk] and satisfies the given [predicate], otherwise [this].
 *
 * @see [takeIf]
 */
public inline fun <V, E> Result<V, E>.toErrorIf(
    predicate: (V) -> Boolean,
    transform: (V) -> E,
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && predicate(value) -> Err(transform(value))
        else -> this
    }
}

/**
 * Returns the supplied [error] if this result [is ok][Result.isOk] and the [value][Result.value]
 * is `null`, otherwise [this].
 *
 * @see [toErrorIf]
 */
public inline fun <V, E> Result<V?, E>.toErrorIfNull(error: () -> E): Result<V, E> {
    contract {
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && value == null -> Err(error())
        isOk && value != null -> this.asOk()
        else -> this.asErr()
    }
}

/**
 * Returns the [transformation][transform] of the [value][Result.value] if this result
 * [is ok][Result.isOk] and _does not_ satisfy the given [predicate], otherwise [this].
 *
 * @see [takeUnless]
 */
public inline fun <V, E> Result<V, E>.toErrorUnless(
    predicate: (V) -> Boolean,
    transform: (V) -> E,
): Result<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && !predicate(value) -> Err(transform(value))
        else -> this
    }
}

/**
 * Returns the supplied [error] unless this result [is ok][Result.isOk] and the
 * [value][Result.value] is `null`, otherwise [this].
 *
 * @see [toErrorUnless]
 */
public inline fun <V, E> Result<V, E>.toErrorUnlessNull(error: () -> E): Result<V, E> {
    contract {
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && value == null -> this
        else -> Err(error())
    }
}
