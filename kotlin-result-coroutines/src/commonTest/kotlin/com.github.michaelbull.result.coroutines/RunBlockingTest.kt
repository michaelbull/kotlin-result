package com.github.michaelbull.result.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Workaround to use suspending functions in unit tests for multiplatform/native projects.
 * Solution was found here: https://github.com/Kotlin/kotlinx.coroutines/issues/885#issuecomment-446586161
 */
expect fun runBlockingTest(
    context: CoroutineContext = EmptyCoroutineContext,
    testBody: suspend CoroutineScope.() -> Unit
)
