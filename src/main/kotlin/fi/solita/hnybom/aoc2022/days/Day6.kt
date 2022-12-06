package fi.solita.hnybom.aoc2022.days

import java.io.File
class Day6 {
    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input6.txt")
            .readText()

    private fun findFirstMatch(windowLength: Int): Int {
        val firstMatch = input.windowed(windowLength).indexOfFirst { slidingWindow ->
            slidingWindow.all { singleChar ->
                slidingWindow.count { singleChar == it } < 2
            }
        }
        return firstMatch
    }

    fun part1(): String {
        val firstMatch = findFirstMatch(4)
        return "First packet start ${firstMatch + 4}"
    }

    fun part2(): String {
        val firstMatch = findFirstMatch(14)
        return "First packet start ${firstMatch + 14}"
    }
}

fun main(args: Array<String>) {
    val d = Day6()
    println(d.part1())
    println(d.part2())
}