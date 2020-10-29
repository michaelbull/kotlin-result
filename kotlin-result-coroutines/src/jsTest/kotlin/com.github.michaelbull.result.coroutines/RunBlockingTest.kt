package com.github.michaelbull.result.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

import kotlin.coroutines.CoroutineContext

actual fun runBlockingTest(context: CoroutineContext, testBody: suspend CoroutineScope.() -> Unit): dynamic =
    GlobalScope.promise(context) { testBody(this) }
