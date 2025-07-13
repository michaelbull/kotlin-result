package com.github.michaelbull.result

import arrow.core.raise.either
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
class BindingBenchmark {

    @Benchmark
    fun bindingSuccess(blackhole: Blackhole) {
        val result: Result<Int, Error> = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun bindingFailure(blackhole: Blackhole) {
        val result: Result<Int, Error> = binding {
            val x = provideX().bind()
            val z = provideZ().bind()
            x + z
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun arrowBindingSuccess(blackhole: Blackhole) {
        val result: Either<Error, Int> = either {
            val x = arrowProvideX().bind()
            val y = arrowProvideY().bind()
            x + y
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun arrowBindingFailure(blackhole: Blackhole) {
        val result: Either<Error, Int> = either {
            val x = arrowProvideX().bind()
            val z = arrowProvideZ().bind()
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

    private fun provideX(): Result<Int, Error> = Ok(1)
    private fun provideY(): Result<Int, Error> = Ok(2)
    private fun provideZ(): Result<Int, Error> = Err(Error)

    private fun arrowProvideX(): Either<Error, Int> = 1.right()
    private fun arrowProvideY(): Either<Error, Int> = 2.right()
    private fun arrowProvideZ(): Either<Error, Int> = Error.left()
}
