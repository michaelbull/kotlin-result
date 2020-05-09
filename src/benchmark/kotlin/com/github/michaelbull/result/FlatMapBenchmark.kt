package com.github.michaelbull.result

import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class FlatMapBenchmark {
    enum class Error { DID_NOT_WORK }

    @Benchmark
    fun success(blackhole: Blackhole) {
        val result: Result<Int, Error> =
            succeed().flatMap { first ->
                succeed().flatMap { second ->
                    Ok(first + second)
                }
            }
        blackhole.consume(result.recover { it.name })
    }

    @Benchmark
    fun failure(blackhole: Blackhole) {
        val result: Result<Int, Error> = succeed()
            .flatMap { first ->
                fail().flatMap { second ->
                    Ok(first + second)
                }
            }
        blackhole.consume(result.recover { it.name })
    }

    @State(Scope.Thread)
    companion object {
        private fun succeed(): Result<Int, Error> = Ok(2)
        private fun fail(): Result<Int, Error> = Err(
            Error.DID_NOT_WORK
        )
    }
}
