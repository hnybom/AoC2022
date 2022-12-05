package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day5 {
    data class Instruction(val amount: Int, val from: Int, val to: Int, )

    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input5.txt")
            .readLines()

    private val instructionsRegEx = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

    private val instructions: List<Instruction>

    private val stackRow = input.indexOfFirst {
        it.trim().startsWith("1")
    }

    init {
        instructions = input.drop(stackRow + 2).mapNotNull {
            val rg = instructionsRegEx.find(it)
            val (amount, from, to) = rg!!.destructured
            Instruction(amount.toInt(), from.toInt(), to.toInt())
        }
    }

    private fun getStacks(): MutableMap<Int, List<Char>> {
        return input.take(stackRow).map {
            it.chunked(4)
        }.flatMap { strings ->
            strings.mapIndexedNotNull { index, s ->
                if(s.isBlank()) null
                else {
                    index + 1 to s[1]
                }
            }

        }.groupBy({ it.first }, {it.second}).toMutableMap()
    }

    fun part1(): String {
        val stacks = getStacks()
        instructions.forEach {
            val elements = stacks[it.from]!!.take(it.amount)
            stacks[it.from] = stacks[it.from]!!.drop(it.amount)
            stacks[it.to] = elements.reversed() + stacks[it.to]!!
        }

        val answer = getTopElementsAsString(stacks)
        return "Top elements: $answer"
    }

    fun part2(): String {
        val stacks = getStacks()

        instructions.forEach {
            val elements = stacks[it.from]!!.take(it.amount)
            stacks[it.from] = stacks[it.from]!!.drop(it.amount)
            stacks[it.to] = elements + stacks[it.to]!!
        }

        val answer = getTopElementsAsString(stacks)
        return "Top elements2: $answer"
    }

    private fun getTopElementsAsString(stacks: MutableMap<Int, List<Char>>): String {
        val answer = stacks.keys.toList().sorted().map {
            stacks[it]!!.first()
        }.joinToString("")
        return answer
    }
}

fun main(args: Array<String>) {
    val d = Day5()
    println(d.part1())
    println(d.part2())
}