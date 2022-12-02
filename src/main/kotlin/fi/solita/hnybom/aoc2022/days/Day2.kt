package fi.solita.hnybom.aoc2022.days

import java.io.File
import java.lang.IllegalArgumentException

class Day2 {

    private val input: List<Pair<String, String>> =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input2.txt")
            .readLines().map {
                val row = it.split(" ")
                row[0] to row[1]
            }

    private val playedSelection = mapOf (
        "X" to 1,
        "Y" to 2,
        "Z" to 3
    )

    private val scoreMap = mapOf(
        "A" to mapOf(
            "X" to 3,
            "Y" to 6,
            "Z" to 0,
        ),
        "B" to mapOf(
            "X" to 0,
            "Y" to 3,
            "Z" to 6,
        ),
        "C" to mapOf(
            "X" to 6,
            "Y" to 0,
            "Z" to 3,
        )
    )

    private val selectionMap = mapOf(
        "A" to mapOf(
            "X" to "C",
            "Y" to "A",
            "Z" to "B",
        ),
        "B" to mapOf(
            "X" to "A",
            "Y" to "B",
            "Z" to "C",
        ),
        "C" to mapOf(
            "X" to "B",
            "Y" to "C",
            "Z" to "A",
        )
    )

    private val outcome = mapOf (
        "X" to 0,
        "Y" to 3,
        "Z" to 6
    )

    private val playedSelection2 = mapOf (
        "A" to 1,
        "B" to 2,
        "C" to 3
    )

    private fun round1(hands: Pair<String, String>) = playedSelection[hands.second]!! + scoreMap[hands.first]!![hands.second]!!

    private fun round2(hands: Pair<String, String>) = outcome[hands.second]!! + playedSelection2[selectionMap[hands.first]!![hands.second]!!]!!

    fun part1(): String {
        val result = input.sumOf { round1(it) }
        return "Total $result"
    }

    fun part2(): String {
        val result = input.sumOf { round2(it) }
        return "Total $result"
    }
}
fun main(args: Array<String>) {
    val d = Day2()
    println(d.part1())
    println(d.part2())
}