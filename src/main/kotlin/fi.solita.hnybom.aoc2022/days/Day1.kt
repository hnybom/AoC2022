package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day1 {
    private val input: List<String> =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input1.txt")
            .readLines()

    tailrec fun partition(left: List<String>, elfIndex: Int, acc: Map<Int, List<Long>>): Map<Int, List<Long>> {
        if(left.isEmpty()) return acc
        val thisBag = left.takeWhile { it.isNotBlank() }.map { it.toLong() }
        return partition( left.drop(thisBag.size + 1), elfIndex + 1,acc + (elfIndex to thisBag))
    }

    val bags = partition(input, 1, emptyMap())

    fun part1(): String {
        val maxCalories = bags.values.maxOfOrNull { it.sum() }
        return "Max calories carried $maxCalories"
    }

    fun part2(): String {
        val topThreeTotal = bags.values.map { it.sum() }.sortedDescending().take(3).sum()
        return "Top 3 total $topThreeTotal"
    }
}

fun main(args: Array<String>) {
    val d = Day1()
    println(d.part1())
    println(d.part2())
}