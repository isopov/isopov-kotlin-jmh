package com.sopovs.moradanen.jmh.kotlin


import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.runBlocking
import org.openjdk.jmh.annotations.*
import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS


//Benchmark                                         (threshold)  Mode  Cnt    Score   Error  Units
//FactorialCoroutinesBenchmark.coroutinesFactorial           16  avgt   15   67.802 ± 0.634  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial           32  avgt   15   47.745 ± 0.146  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial           64  avgt   15   37.635 ± 0.173  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial          128  avgt   15   33.479 ± 0.322  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial          256  avgt   15  174.399 ± 2.444  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial          512  avgt   15  103.907 ± 1.271  ms/op
//FactorialCoroutinesBenchmark.coroutinesFactorial         1024  avgt   15   62.985 ± 0.421  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial             16  avgt   15    8.951 ± 0.176  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial             32  avgt   15    8.901 ± 0.112  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial             64  avgt   15    8.922 ± 0.088  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial            128  avgt   15    9.090 ± 0.081  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial            256  avgt   15    9.308 ± 0.103  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial            512  avgt   15    9.688 ± 0.110  ms/op
//FactorialCoroutinesBenchmark.forkJoinFactorial           1024  avgt   15   10.664 ± 0.188  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial           16  avgt   15   61.210 ± 0.128  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial           32  avgt   15   44.507 ± 0.103  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial           64  avgt   15   36.203 ± 0.070  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial          128  avgt   15   32.537 ± 0.181  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial          256  avgt   15  172.387 ± 2.303  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial          512  avgt   15  103.256 ± 0.611  ms/op
//FactorialCoroutinesBenchmark.threadPoolFactorial         1024  avgt   15   63.150 ± 0.376  ms/op
//LinearFactorialBenchmark.linearFactorial                  N/A  avgt   15  226.578 ± 1.157  ms/op


const val FACTORIAL = 32768;

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
open class FactorialCoroutinesBenchmark {


    @Param("16", "32", "64", "128", "256", "512", "1024")
    var threshold = 0

    @Benchmark
    fun coroutinesFactorial(): BigInteger = runBlocking {
        val context = newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors(), "coroutines")

        val steps = Array(FACTORIAL / threshold) {
            async(context) {
                var result = BigInteger.ONE
                for (i in (it * threshold + 1) .. (it + 1) * threshold) {
                    result *= i.toBigInteger()
                }
                result
            }
        }


        var result = BigInteger.ONE
        for (i in 1..(FACTORIAL / threshold)) {
            result *= steps[i - 1].await()
        }
        context.close()
        (context.executor as ExecutorService).awaitTermination(1, SECONDS)
        result
    }

    @Benchmark
    fun threadPoolFactorial(): BigInteger {
        val pool = newFixedThreadPool(Runtime.getRuntime().availableProcessors())

        val steps = Array(FACTORIAL / threshold) {
            pool.submit(Callable<BigInteger> {
                var result = BigInteger.ONE
                for (i in (it * threshold + 1) .. (it + 1) * threshold) {
                    result *= i.toBigInteger()
                }
                result
            })
        }

        var result = BigInteger.ONE
        for (i in 1..(FACTORIAL / threshold)) {
            result *= steps[i - 1].get()
        }

        pool.shutdown()
        pool.awaitTermination(1, SECONDS)
        return result
    }

    @Benchmark
    fun forkJoinFactorial(): BigInteger {
        val forkJoinPool = ForkJoinPool(Runtime.getRuntime().availableProcessors())
        val future = forkJoinPool.submit(FactorialRecursiveTask(1, FACTORIAL + 1, threshold))
        val result = future.get()
        forkJoinPool.shutdown()
        forkJoinPool.awaitTermination(1, SECONDS)
        return result
    }


    internal inner class FactorialRecursiveTask(private val lo: Int, private val hi: Int, private val threshold: Int) : RecursiveTask<BigInteger>() {

        override fun compute(): BigInteger {
            if (hi - lo < threshold) {
                var result = BigInteger.valueOf(lo.toLong())
                for (i in lo + 1 until hi) {
                    result *= i.toBigInteger()
                }
                return result
            } else {
                val mid = (lo + hi).ushr(1)

                val f1 = FactorialRecursiveTask(lo, mid, threshold)
                f1.fork()
                val f2 = FactorialRecursiveTask(mid, hi, threshold)
                return f2.compute().multiply(f1.join())
            }
        }
    }


}

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
open class LinearFactorialBenchmark {

    @Benchmark
    fun linearFactorial(): BigInteger {
        var result = BigInteger.ONE
        for (i in 1..FACTORIAL) {
            result *= i.toBigInteger()
        }
        return result
    }
}

