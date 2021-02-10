package com.github.michaelbull.result

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
import com.github.michaelbull.result.coroutines.binding.binding as coroutineBinding

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class SuspendBindingBenchmark {

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

    private object Error

    private val time = 100L

    private fun nonSuspend() = binding<Int, Error> {
        val x = provideXBlocking().bind()
        val y = provideYBlocking().bind()
        x + y
    }

    private suspend fun withSuspend(): Result<Int, Error> {
        return coroutineBinding {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }
    }

    private suspend fun withAsyncSuspend(): Result<Int, Error> {
        return coroutineScope {
            coroutineBinding {
                val x = async { provideX().bind() }
                val y = async { provideY().bind() }
                x.await() + y.await()
            }
        }
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
}
