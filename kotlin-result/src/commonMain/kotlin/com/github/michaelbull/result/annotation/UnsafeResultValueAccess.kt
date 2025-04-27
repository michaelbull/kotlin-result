package com.github.michaelbull.result.annotation

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import kotlin.RequiresOptIn.Level.ERROR
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.PROPERTY

/**
 * Marks access to [Result.value] as unsafe. The [Result] must be guaranteed to be [Ok].
 *
 * Ensure that you verify the state of the [Result] by using [Result.isOk] before accessing its
 * [Result.value].
 *
 * Alternatively, consider using [mapBoth] to safely handle the result.
 */
@RequiresOptIn(
    level = ERROR,
    message = "Accessing `Result.value` without an explicit `Result.isOk` check is unsafe. Opt-in only when the result is guaranteed to be `Ok`.",
)
@Retention(BINARY)
@Target(PROPERTY)
@MustBeDocumented
public annotation class UnsafeResultValueAccess
