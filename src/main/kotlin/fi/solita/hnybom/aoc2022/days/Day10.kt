package fi.solita.hnybom.aoc2022.days

import java.io.File
import java.lang.IllegalArgumentException

class Day10 {

    enum class COMMAND(val cycles: Int) {
        ADDX(2), NOOP(1)
    }

    data class Instruction(val c: COMMAND, val amount: Int?)
    data class CycleCounter(val inputIndex: Int, val x: Int, val innerCycle: Int)

    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input10.txt")
            .readLines()
            .map {
                when {
                    it.startsWith("noop") -> Instruction(COMMAND.NOOP, null)
                    it.startsWith("addx") -> Instruction(COMMAND.ADDX, it.split(" ")[1].toInt())
                    else -> throw IllegalArgumentException()
                }
            }

    private val interestingCycles = listOf(20, 60, 100, 140, 180, 220)
    private val drawingCycles = listOf(0, 40, 80, 120, 160, 200)

    // Tripe 1. input index 2. X amount 3. current commandCycle
    fun generateCycles() : Sequence<Int> {
        return generateSequence(Triple(0, 1, 1)) {
            val instruction = input[it.first]
            if(instruction.c == COMMAND.NOOP) {
                Triple(it.first + 1, it.second, 1)
            } else {
                if(it.third == instruction.c.cycles) {
                    Triple(it.first + 1, it.second + instruction.amount!!, 1)
                } else {
                    Triple(it.first, it.second, it.third + 1)
                }
            }
        }.map { it.second }
    }

    private val crtRow = "........................................"

    fun part1(): String {
        val cycleValues = generateCycles()
        val cycleAmounts = interestingCycles.map {
            cycleValues.take(it).last() * it
        }

        val sumOfCycles = cycleAmounts.sumOf { it }
        return "Sum of cycles: $sumOfCycles"
    }

    fun part2() {
        val cycleValues = generateCycles()
        val rows = drawingCycles.map {
            cycleValues.drop(it).take(40).foldIndexed(crtRow) { i, row, location ->
                val litPixels = (location - 1)..(location + 1)
                if(litPixels.contains(i)) {
                    row.substring(0, i) + "#" + row.substring(i + 1)
                } else row
            }
        }
        rows.forEach {
            println(it)
        }
    }
}
fun main(args: Array<String>) {
    val d = Day10()
    println(d.part1())
    d.part2()
}
