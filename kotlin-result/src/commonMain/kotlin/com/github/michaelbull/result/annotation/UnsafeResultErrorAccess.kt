package com.github.michaelbull.result.annotation

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import kotlin.RequiresOptIn.Level.ERROR
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.PROPERTY

/**
 * Marks access to [Result.error] as unsafe. The [Result] must be guaranteed to be [Err].
 *
 * Ensure that you verify the state of the [Result] by using [Result.isErr] before accessing its
 * [Result.error].
 *
 * Alternatively, consider using [mapBoth] to safely handle the result.
 */
@RequiresOptIn(
    level = ERROR,
    message = "Accessing `Result.error` without an explicit `Result.isErr` check is unsafe. Opt-in only when the result is guaranteed to be `Err`.",
)
@Retention(BINARY)
@Target(PROPERTY)
@MustBeDocumented
public annotation class UnsafeResultErrorAccess
