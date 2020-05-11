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
class AndThenBenchmark {

    private object Error

    @Benchmark
    fun success(blackhole: Blackhole) {
        val result =
            provideX().andThen { first ->
                provideY().andThen { second ->
                    Ok(first + second)
                }
            }

        blackhole.consume(result)
    }

    @Benchmark
    fun failure(blackhole: Blackhole) {
        val result =
            provideX().andThen { first ->
                provideZ().andThen { second ->
                    Ok(first + second)
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
