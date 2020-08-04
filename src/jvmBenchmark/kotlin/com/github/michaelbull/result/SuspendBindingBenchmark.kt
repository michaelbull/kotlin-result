package com.github.michaelbull.result

import kotlinx.coroutines.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Threads(Threads.MAX)
class SuspendBindingBenchmark {

    private object Error

    @Benchmark
    fun nonSuspendableBinding(blackhole: Blackhole) {
        val result = binding<Int, Error> {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }
        blackhole.consume(result)
    }

    @Benchmark
    fun suspendableBinding(blackhole: Blackhole) {
        val result = GlobalScope.async(Dispatchers.Default) {
            com.github.michaelbull.result.coroutines.binding<Int, Error> {
                val x = suspendedProvideX().bind()
                val y = suspendedProvideY().bind()
                x + y
            }
        }
        runBlocking {
            blackhole.consume(result.await())
        }
    }

    @State(Scope.Thread)
    private companion object {

        val time = 100L

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
