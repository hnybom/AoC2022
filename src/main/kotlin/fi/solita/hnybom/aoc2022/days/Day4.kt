package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day4 {
    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input4.txt")
            .readLines()
            .map {
                it.split(",")
            }
            .map {
                val firstRange = it[0].split("-")
                val secondRange = it[1].split("-")
                firstRange[0].toInt()..firstRange[1].toInt() to secondRange[0].toInt()..secondRange[1].toInt()
            }

    fun part1(): String {
        val totalContainment = input.count { rangePair ->
            rangePair.first.all { rangePair.second.contains(it) }
                    || rangePair.second.all { rangePair.first.contains(it) }
        }
        return "Total overlapping $totalContainment"
    }

    fun part2(): String {
        val totalContainment = input.count { rangePair ->
            rangePair.first.any { rangePair.second.contains(it) }
                    || rangePair.second.any { rangePair.first.contains(it) }
        }
        return "Some overlapping $totalContainment"
    }
}
fun main(args: Array<String>) {
    val d = Day4()
    println(d.part1())
    println(d.part2())
}
