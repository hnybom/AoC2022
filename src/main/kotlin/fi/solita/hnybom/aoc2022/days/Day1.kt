package fi.solita.hnybom.aoc2022.days

import java.io.File
class Day1 {
    private val input: List<String> =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input1.txt")
            .readText().split("\n\n")

    private val bags = input.map { it.split("\n").sumOf { it.toLong() } }

    fun part1(): String {
        val maxCalories = bags.max()
        return "Max calories carried $maxCalories"
    }

    fun part2(): String {
        val topThreeTotal = bags.sortedDescending().take(3).sum()
        return "Top 3 total $topThreeTotal"
    }
}
fun main(args: Array<String>) {
    val d = Day1()
    println(d.part1())
    println(d.part2())
}