package com.github.michaelbull.result

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class ArrowBindingBenchmark {

    @Benchmark
    fun arrowFlatMapSuccess(blackhole: Blackhole) {
        val result = arrowProvideX().flatMap { x ->
            arrowProvideY().flatMap { y ->
                (x + y).right()
            }
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun arrowFlatMapFailure(blackhole: Blackhole) {
        val result = arrowProvideX().flatMap { x ->
            arrowProvideZ().flatMap { z ->
                (x + z).right()
            }
        }

        blackhole.consume(result)
    }

    private object Error

    private fun arrowProvideX(): Either<Error, Int> = 1.right()
    private fun arrowProvideY(): Either<Error, Int> = 2.right()
    private fun arrowProvideZ(): Either<Error, Int> = Error.left()
}
