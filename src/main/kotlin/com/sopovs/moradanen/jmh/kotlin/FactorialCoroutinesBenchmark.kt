package com.sopovs.moradanen.jmh.kotlin


import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.runBlocking
import org.openjdk.jmh.annotations.*
import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

//Benchmark                                         (arg)  Mode  Cnt    Score   Error  Units
//FactorialCoroutinesBenchmark.coroutinesFactorial    512  avgt   15    5.552 ± 0.263  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial   1024  avgt   15   16.330 ± 0.241  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial   2048  avgt   15   59.499 ± 2.062  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial   4096  avgt   15  235.284 ± 5.435  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial    512  avgt   15    3.910 ± 0.148  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial   1024  avgt   15   13.214 ± 0.256  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial   2048  avgt   15   56.221 ± 1.222  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial   4096  avgt   15  229.299 ± 3.997  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
open class FactorialCoroutinesBenchmark {

    @Param("512", "1024", "2048", "4096")
    var arg = 0

    @Benchmark
    fun linearFactorial(): BigInteger {
        var result = BigInteger.ONE
        for (i in 1..arg * 10) {
            result *= i.toBigInteger()
        }
        return result
    }

    @Benchmark
    fun coroutinesFactorial(): BigInteger = runBlocking {
        val context = newFixedThreadPoolContext(4, "coroutines")
        val steps = Array(arg) {
            async(context) {
                var result = BigInteger.ONE
                for (i in it * 10 + 1..(it + 1) * 10) {
                    result *= i.toBigInteger()
                }
                result
            }
        }


        var result = BigInteger.ONE
        for (i in 1..arg) {
            result *= steps[i - 1].await()
        }
        context.close()
        (context.executor as ExecutorService).awaitTermination(1, SECONDS)
        result
    }

    @Benchmark
    fun threadPoolFactorial(): BigInteger {
        val pool = newFixedThreadPool(4)

        val steps = Array(arg) {
            pool.submit(Callable<BigInteger> {
                var result = BigInteger.ONE
                for (i in it * 10 + 1..(it + 1) * 10) {
                    result *= i.toBigInteger()
                }
                result
            })
        }

        var result = BigInteger.ONE
        for (i in 1..arg) {
            result *= steps[i - 1].get()
        }

        pool.shutdown()
        pool.awaitTermination(1, SECONDS)
        return result
    }

}