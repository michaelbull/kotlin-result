package com.github.michaelbull.result

import kotlinx.coroutines.CoroutineScope

/**
 * Workaround to use suspending functions in unit tests for multiplatform/native projects.
 * Solution was found here: https://github.com/Kotlin/kotlinx.coroutines/issues/885#issuecomment-446586161
 */
expect fun runBlockingTest(block: suspend (scope : CoroutineScope) -> Unit)
