package com.github.michaelbull.result

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class BindingBenchmark {

    private object Error

    @Benchmark
    fun bindingSuccess(blackhole: Blackhole) {
        val result = binding<Int, Error> {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun bindingFailure(blackhole: Blackhole) {
        val result = binding<Int, Error> {
            val x = provideX().bind()
            val z = provideZ().bind()
            x + z
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun andThenSuccess(blackhole: Blackhole) {
        val result = provideX().andThen { x ->
            provideY().andThen { y ->
                Ok(x + y)
            }
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun andThenFailure(blackhole: Blackhole) {
        val result = provideX().andThen { x ->
            provideZ().andThen { z ->
                Ok(x + z)
            }
        }

        blackhole.consume(result)
    }

    @State(Scope.Thread)
    private companion object {
        private fun provideX(): Result<Int, Error> = Ok(1)
        private fun provideY(): Result<Int, Error> = Ok(2)
        private fun provideZ(): Result<Int, Error> = Err(Error)
    }
}
