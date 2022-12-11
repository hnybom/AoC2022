package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day11 {

    data class Monkey(val id: Int, var items: List<Long>, val worryOperation: String, val worryMultiplier: String,
                      val testDivision: Long, val trueTestTarget: Int,
                      val falseTestTarget: Int, var itemsInspected: Long = 0) {

        fun calculateNewWorry(item: Long) : Long {
           val operationNumber = if(worryMultiplier == "old")  item else worryMultiplier.toLong()
            return when(worryOperation)  {
                "+" -> item + operationNumber
                "*" -> item * operationNumber
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun getMonkeyMap() = File("/Users/hnybom/work/AoC2022/src/main/resources/input11.txt")
            .readText().split("\n\n").mapIndexed { i, monkeyDataString ->
                val monkeyData = monkeyDataString.split("\n")
                val startingItems = monkeyData[1].split(":")[1].split(",").map { it.trim().toLong() }
                val operationSplit = monkeyData[2].split(" ")
                val worryMultiplierString = operationSplit.last().trim()
                val operation = operationSplit[operationSplit.size - 2].trim()
                val testDivision = monkeyData[3].split(" ").last().toLong()
                val trueTarget = monkeyData[4].split(" ").last().toInt()
                val falseTarget = monkeyData[5].split(" ").last().toInt()
                i to Monkey(
                    id = i,
                    items = startingItems,
                    worryOperation = operation,
                    worryMultiplier = worryMultiplierString,
                    testDivision = testDivision,
                    trueTestTarget = trueTarget,
                    falseTestTarget = falseTarget
                )
            }.toMap()

    fun simulateMonkey(map: Map<Int, Monkey>, monkey: Monkey) {
        monkey.items.forEach {
            val itemWorry = monkey.calculateNewWorry(it)
            val bored = itemWorry / 3
            val target = when(bored % monkey.testDivision == 0L) {
                true -> map[monkey.trueTestTarget]!!
                false -> map[monkey.falseTestTarget]!!
            }
            target.items = target.items + bored
            monkey.itemsInspected = monkey.itemsInspected + 1
        }
        monkey.items = emptyList()
    }

    private fun simulateMonkey2(map: Map<Int, Monkey>, monkey: Monkey) {
        val superModulo = map.values.map { it.testDivision }.reduce { acc, l -> acc * l }

        monkey.items.forEach {
            val itemWorry = monkey.calculateNewWorry(it)
            val newItemWorry = itemWorry % superModulo
            val target = when(newItemWorry % monkey.testDivision == 0L) {
                true -> map[monkey.trueTestTarget]!!
                false -> map[monkey.falseTestTarget]!!
            }
            target.items = target.items + newItemWorry
            monkey.itemsInspected = monkey.itemsInspected + 1
        }
        monkey.items = emptyList()
    }

    tailrec fun monkeySimulator(map: Map<Int, Monkey>, rounds: Int, monkeySimulator: (Map<Int, Monkey>, Monkey) -> Unit) {
        if(rounds == 0) return
        map.values.forEach {
            monkeySimulator(map, it)
        }
        val remainingRounds = rounds - 1
        monkeySimulator(map, remainingRounds, monkeySimulator)
    }

    fun part1(): String {
        val map = getMonkeyMap()
        monkeySimulator(map, 20, this::simulateMonkey)
        val answer = map.values.sortedByDescending { it.itemsInspected }
            .take(2).map { it.itemsInspected  }.reduce { acc, i -> acc * i }
        return "First part monkey business $answer"
    }

    fun part2(): String {
        val map = getMonkeyMap()

        monkeySimulator(map, 10000, this::simulateMonkey2)
        val answer = map.values.sortedByDescending { it.itemsInspected }
            .take(2).map { it.itemsInspected  }.reduce { acc, i -> acc * i }
        return "Second part monkey business $answer"
        return ""
    }
}
fun main(args: Array<String>) {
    val d = Day11()
    println(d.part1())
    println(d.part2())
}