package fi.solita.hnybom.aoc2022.days

import java.io.File
import java.lang.IllegalArgumentException

class Day9 {

    data class Coordinate(val x: Int, val y: Int)

    enum class DIRECTION(val direction: Char) {
        UP('U'), RIGHT('R'), DOWN('D'), LEFT('L');

        companion object {
            fun fromChar(char: Char) = when (char) {
                    'U' -> UP
                    'R' -> RIGHT
                    'D' -> DOWN
                    'L' -> LEFT
                    else -> throw IllegalArgumentException("Not a valid direction $char")
            }
        }
    }

    data class Movement(val d: DIRECTION, val amount: Int)

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input9.txt")
            .readLines().map {
                val split = it.split(" ")
                Movement(DIRECTION.fromChar(split[0][0]), split[1].toInt())
            }

    fun simulateMoves(moves: List<Movement>,
                      headLocation: Coordinate = Coordinate(0, 0),
                      tailLocation: Coordinate = Coordinate(0, 0)) : Map<Coordinate, Int> {



    }

    fun part1(): String {

        return ""
    }

    fun part2(): String {

        return ""
    }
}
fun main(args: Array<String>) {
    val d = Day9()
    println(d.part1())
    println(d.part2())
}