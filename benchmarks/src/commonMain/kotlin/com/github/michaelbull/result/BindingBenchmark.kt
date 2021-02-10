package com.github.michaelbull.result

import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.State
import kotlinx.benchmark.Scope

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
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

    private companion object {
        private fun provideX(): Result<Int, Error> = Ok(1)
        private fun provideY(): Result<Int, Error> = Ok(2)
        private fun provideZ(): Result<Int, Error> = Err(Error)
    }
}
