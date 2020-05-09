package com.natpryce

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MILLISECONDS)
class FlatMapBenchmark {
    enum class Error { DID_NOT_WORK }

    @Benchmark
    fun success(blackhole: Blackhole) {
        val result: Result<Int, Error> = succeed()
            .flatMap { first ->
                succeed().flatMap { second ->
                    Success(first + second)
                }
            }
        blackhole.consume(result.recover { it.name })
    }

    @Benchmark
    fun failure(blackhole: Blackhole) {
        val result: Result<Int, Error> = succeed()
            .flatMap { first ->
                fail().flatMap { second ->
                    Success(first + second)
                }
            }
        blackhole.consume(result.recover { it.name })
    }

    @State(Scope.Thread)
    companion object {
        private fun succeed(): Result<Int, Error> = Success(2)
        private fun fail(): Result<Int, Error> = Failure(
            Error.DID_NOT_WORK
        )
    }
}
