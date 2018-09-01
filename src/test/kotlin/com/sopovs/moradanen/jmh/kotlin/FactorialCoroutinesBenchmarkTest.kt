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
        benchmark.arg = 3
    }

    @Test
    fun threadPoolFactorialTest() {
        assertEquals(
                "265252859812191058636308480000000".toBigInteger(),
                benchmark.threadPoolFactorial()
        )
    }

    @Test
    fun coroutinesFactorialTest() {
        assertEquals(
                "265252859812191058636308480000000".toBigInteger(),
                benchmark.coroutinesFactorial()
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