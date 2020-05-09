package com.github.michaelbull.result

import com.github.michaelbull.result.bind.binding
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class BindBenchmark {
    enum class Error { DID_NOT_WORK }

    @Benchmark
    fun success(blackhole: Blackhole) {
        val result = binding<Int, Error> {
            val first = succeed().bind()
            val second = succeed().bind()
            first + second
        }
        blackhole.consume(result.recover { it.name })
    }

    @Benchmark
    fun failure(blackhole: Blackhole) {
        val result = binding<Int, Error> {
            val first = succeed().bind()
            val second = fail().bind()
            first + second
        }
        blackhole.consume(result.recover { it.name })
    }

    @State(Scope.Thread)
    companion object {
        private fun succeed(): Result<Int, Error> = Ok(2)
        private fun fail(): Result<Int, Error> = Err(Error.DID_NOT_WORK)
    }
}
