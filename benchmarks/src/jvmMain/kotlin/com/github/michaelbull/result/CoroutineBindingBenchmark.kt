package com.github.michaelbull.result

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import com.github.michaelbull.result.coroutines.coroutineBinding
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class CoroutineBindingBenchmark {

    @Benchmark
    fun nonSuspendableBinding(blackhole: Blackhole) {
        blackhole.consume(nonSuspend().get())
    }

    @Benchmark
    fun suspendableBinding(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(withSuspend().get())
        }
    }

    @Benchmark
    fun asyncSuspendableBinding(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(withAsyncSuspend().get())
        }
    }

    @Benchmark
    fun arrowNonSuspendableBinding(blackhole: Blackhole) {
        blackhole.consume(nonSuspend().get())
    }

    @Benchmark
    fun arrowSuspendableBinding(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(withSuspend().get())
        }
    }

    @Benchmark
    fun arrowAsyncSuspendableBinding(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(withAsyncSuspend().get())
        }
    }

    private object Error

    private val time = 100L

    private fun nonSuspend(): Result<Int, Error> = binding {
        val x = provideXBlocking().bind()
        val y = provideYBlocking().bind()
        x + y
    }

    private suspend fun withSuspend(): Result<Int, Error> = coroutineBinding {
        val x = provideX().bind()
        val y = provideY().bind()
        x + y
    }

    private suspend fun withAsyncSuspend(): Result<Int, Error> = coroutineBinding {
        val x = async { provideX().bind() }
        val y = async { provideY().bind() }
        x.await() + y.await()
    }

    private fun provideXBlocking(): Result<Int, Error> {
        Thread.sleep(time)
        return Ok(1)
    }

    private fun provideYBlocking(): Result<Int, Error> {
        Thread.sleep(time)
        return Ok(2)
    }

    private suspend fun provideX(): Result<Int, Error> {
        delay(time)
        return Ok(1)
    }

    private suspend fun provideY(): Result<Int, Error> {
        delay(time)
        return Ok(2)
    }

    private fun arrowNonSuspend(): Either<Error, Int> = either {
        val x = arrowProvideXBlocking().bind()
        val y = arrowProvideYBlocking().bind()
        x + y
    }

    private suspend fun arrowWithSuspend(): Either<Error, Int> = either {
        val x = arrowProvideX().bind()
        val y = arrowProvideY().bind()
        x + y
    }

    private suspend fun arrowWithAsyncSuspend(): Either<Error, Int> = either {
        coroutineScope {
            val x = async { arrowProvideX().bind() }
            val y = async { arrowProvideY().bind() }
            x.await() + y.await()
        }
    }

    private fun arrowProvideXBlocking(): Either<Error, Int> {
        Thread.sleep(time)
        return 1.right()
    }

    private fun arrowProvideYBlocking(): Either<Error, Int> {
        Thread.sleep(time)
        return 2.right()
    }

    private suspend fun arrowProvideX(): Either<Error, Int> {
        delay(time)
        return 1.right()
    }

    private suspend fun arrowProvideY(): Either<Error, Int> {
        delay(time)
        return 2.right()
    }
}
