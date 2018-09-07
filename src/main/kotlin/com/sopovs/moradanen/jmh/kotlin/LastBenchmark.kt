package com.sopovs.moradanen.jmh.kotlin

import com.google.common.collect.Lists
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit.NANOSECONDS


@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
open class LastBenchmark {
    //    @Param("512", "1024", "2048", "4096")
    @Param("4096")
    var size = 0
    private lateinit var list: List<Int>
    private lateinit var iterable: Iterable<Int>

    @Setup
    fun setup() {
        val arrayList = ArrayList<Int>()
        for (i in 1..size)
            arrayList.add(0)
        list = arrayList
        iterable = arrayList

    }


    @Benchmark
    fun lastIterable(): Int = iterable.last { it == 0 }

    @Benchmark
    fun lastGuavaReverseStream(): Int = Lists.reverse(list)
            .stream()
            .filter({ it == 0 })
            .findFirst().get()


    @Benchmark
    fun lastList(): Int = list.last { it == 0 }

    @Benchmark
    fun myList(): Int = iterable.myLast { it == 0 }

}

inline fun <T> Iterable<T>.myLast(noinline predicate: (T) -> Boolean): T =
        if (this is List) this.last(predicate) else this.last(predicate)
