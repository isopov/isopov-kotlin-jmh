package com.sopovs.moradanen.jmh.kotlin

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import one.util.streamex.StreamEx
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit.NANOSECONDS

//Benchmark                          (size)  Mode  Cnt      Score     Error  Units
//LastBenchmark.lasStreamExReversed    4096  avgt   15     98.825 ±   0.230  ns/op
//LastBenchmark.lastGuava              4096  avgt   15  26735.961 ±  20.377  ns/op
//LastBenchmark.lastGuavaReverse       4096  avgt   15     15.685 ±   0.228  ns/op
//LastBenchmark.lastIterable           4096  avgt   15   3505.387 ± 342.838  ns/op
//LastBenchmark.lastList               4096  avgt   15      3.931 ±   0.028  ns/op
//LastBenchmark.lastStreamEx           4096  avgt   15  63351.924 ± 867.985  ns/op
//LastBenchmark.myLast                 4096  avgt   15      3.935 ±   0.020  ns/op
//LastBenchmark.myLastInline           4096  avgt   15      3.916 ±   0.007  ns/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
open class LastBenchmark {
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
    fun lastList(): Int = list.last { it == 0 }

    @Benchmark
    fun lastGuava(): Int = Iterables.getLast(Iterables.filter(list) { it == 0 })

    @Benchmark
    fun lastGuavaReverse(): Int? =
            Iterables.getFirst(
                    Iterables.filter(
                            Lists.reverse(list)
                    ) { it == 0 },
                    0
            )

    @Benchmark
    fun lastStreamEx(): Int = StreamEx.of(list).filter { it == 0 }.last()

    @Benchmark
    fun lasStreamExReversed(): Int = StreamEx.ofReversed(list).filter { it == 0 }.first()

    @Benchmark
    fun myLast(): Int = iterable.myLast { it == 0 }


    @Benchmark
    fun myLastInline(): Int = iterable.myLastInline { it == 0 }
}

inline fun <T> Iterable<T>.myLastInline(predicate: (T) -> Boolean): T =
        if (this is List) this.last(predicate) else this.last(predicate)

fun <T> Iterable<T>.myLast(predicate: (T) -> Boolean): T =
        if (this is List) this.last(predicate) else this.last(predicate)
