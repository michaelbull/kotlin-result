package com.github.michaelbull.result.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

actual fun runBlockingTest(context: CoroutineContext, testBody: suspend CoroutineScope.() -> Unit) {
    return runBlocking {
        testBody(this)
    }
}
