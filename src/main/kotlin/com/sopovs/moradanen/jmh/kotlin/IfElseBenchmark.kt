package com.sopovs.moradanen.jmh.kotlin

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
open class IfElseBenchmark {

    var foo: Int? = 42

    @Benchmark
    fun ifMethodPositive() =
            if (foo == 42) 1 else 0

    @Benchmark
    fun ifMethodNegative() =
            if (foo == 43) 1 else 0

    @Benchmark
    fun whenMethodNegative() =
            when (foo) {
                43 -> 1
                else -> 0
            }

    @Benchmark
    fun whenMethodPositive() =
            when (foo) {
                42 -> 1
                else -> 0
            }


}
