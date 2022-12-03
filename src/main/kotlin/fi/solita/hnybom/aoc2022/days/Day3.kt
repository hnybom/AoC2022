package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day3 {

    private val inputLines = File("/Users/hnybom/work/AoC2022/src/main/resources/input3.txt")
        .readLines()

    private val input1: List<Pair<String, String>> =
        inputLines
            .map {
                it.substring(0, it.length / 2) to it.substring(it.length / 2)
            }

    private val input2 = inputLines.windowed(3, 3)

    private fun calculatePriority(x: Char): Int {
        return if(x.isUpperCase()) x.code - 38
        else x.code - 96
    }

    fun part1(): String {
        val totalPriority = input1.flatMap {
            it.first.toCharArray().intersect(it.second.toCharArray().toList())
        }.sumOf { calculatePriority(it) }

        return "Total priority: $totalPriority"
    }

    fun part2(): String {
        val totalPriority = input2.sumOf { group ->
            val first = group[0].toCharArray().toList()
            val second = group[1].toCharArray().toList()
            val third = group[2].toCharArray().toList()
            val intersect = first.intersect(second).intersect(third)
            intersect.sumOf {
                calculatePriority(it)
            }
        }

        return "Total priority: $totalPriority"
    }
}

fun main(args: Array<String>) {
    val d = Day3()
    println(d.part1())
    println(d.part2())
}