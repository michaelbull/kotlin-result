package com.github.michaelbull.result

import kotlinx.coroutines.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class SuspendBindingBenchmark {

    private object Error

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

    @State(Scope.Thread)
    private companion object {

        val time = 100L

        fun nonSuspend() = binding<Int, Error> {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        suspend fun withSuspend() =
            com.github.michaelbull.result.coroutines.binding<Int, Error> {
                val x = suspendedProvideX().bind()
                val y = suspendedProvideY().bind()
                x + y
            }

        suspend fun withAsyncSuspend() = coroutineScope {
            com.github.michaelbull.result.coroutines.binding<Int, Error> {
                val x = async { suspendedProvideX().bind() }
                val y = async { suspendedProvideY().bind() }
                x.await() + y.await()
            }
        }

        private fun provideX(): Result<Int, Error> {
            Thread.sleep(time)
            return Ok(1)
        }
        private fun provideY(): Result<Int, Error> {
            Thread.sleep(time)
            return Ok(2)
        }
        private suspend fun suspendedProvideX(): Result<Int, Error> {
            delay(time)
            return Ok(1)
        }
        private suspend fun suspendedProvideY(): Result<Int, Error> {
            delay(time)
            return Ok(2)
        }
    }

}
