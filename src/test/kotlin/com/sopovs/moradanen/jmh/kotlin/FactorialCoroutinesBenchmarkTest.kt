package com.sopovs.moradanen.jmh.kotlin

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.lang.management.ManagementFactory

class FactorialCoroutinesBenchmarkTest {

    private val benchmark = FactorialCoroutinesBenchmark()

    @Before
    fun setup() {
        benchmark.factorial = 64
        benchmark.threshold = 2
    }

    @Test
    fun linearFactorialTest() {
        val linearBenchmark = LinearFactorialBenchmark()
        linearBenchmark.factorial = 64

        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                linearBenchmark.linearFactorial()
        )
    }

    @Test
    fun linearRecursiveFactorialTest() {
        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                benchmark.linearRecursiveFactorial()
        )
    }


    @Test
    fun coroutinesRecursiveFactorialTest() {
        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                benchmark.coroutinesRecursiveFactorial()
        )
    }


    @Test
    fun linearThresholdFactorialTest() {
        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                benchmark.linearThresholdFactorial()
        )
    }

    @Test
    fun threadPoolFactorialTest() {
        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                benchmark.threadPoolFactorial()
        )
    }

    @Test
    fun coroutinesFactorialTest() {
        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                benchmark.coroutinesFactorial()
        )
    }

    @Test
    fun forkJoinFactorialTest() {
        assertEquals(
                "126886932185884164103433389335161480802865516174545192198801894375214704230400000000000000".toBigInteger(),
                benchmark.forkJoinFactorial()
        )
    }


    @Ignore //non-stable test :-(
    @Test
    fun threadPoolFactorialDoesNotLeaveThreads() {
        val threads = threadsCount()
        benchmark.threadPoolFactorial()
        assertEquals(threads, threadsCount())
    }

    @Ignore //non-stable test :-(
    @Test
    fun coroutinesFactorialDoesNotLeaveThreads() {
        val threads = threadsCount()
        benchmark.coroutinesFactorial()
        assertEquals(threads, threadsCount())
    }

    fun threadsCount() = ManagementFactory.getThreadMXBean().getThreadCount()

}