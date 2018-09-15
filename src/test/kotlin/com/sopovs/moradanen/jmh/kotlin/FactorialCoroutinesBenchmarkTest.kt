package com.sopovs.moradanen.jmh.kotlin

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.lang.management.ManagementFactory

class FactorialCoroutinesBenchmarkTest {

    private val benchmark = FactorialCoroutinesBenchmark()
    private val result = LinearFactorialBenchmark().linearFactorial()

    @Before
    fun setup() {
        benchmark.threshold = 512
    }

    @Test
    fun threadPoolFactorialTest() {
        assertEquals(
                result,
                benchmark.threadPoolFactorial()
        )
    }

    @Test
    fun coroutinesFactorialTest() {
        assertEquals(
                result,
                benchmark.coroutinesFactorial()
        )
    }

    @Test
    fun forkJoinFactorialTest() {
        assertEquals(
                result,
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