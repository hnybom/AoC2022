package fi.solita.hnybom.aoc2022.utils

class Helpers {

    class PerformanceTime(val start : Long = System.currentTimeMillis()) {

        fun time() = println("Elapsed: ${System.currentTimeMillis() - start}ms")

    }
}