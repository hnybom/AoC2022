package fi.solita.hnybom.aoc2022.days

import fi.solita.hnybom.aoc2022.utils.Helpers
import java.io.File
class Day14 {

    data class Occupied(val x: Int, val y: Int)

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input14.txt")
            .readLines()
            .flatMap { line ->
                line.split("->").map {
                    val s = it.trim().split(",")
                    Coordinate(s[0].toInt(), s[1].toInt())
                }.windowed(2).flatMap {
                    val from = it.first()
                    val to = it[1]
                    if (from.first == to.first) {
                        (minOf(from.second, to.second)..maxOf(from.second, to.second)).map { y ->
                            Occupied(from.first, y)
                        }
                    } else {
                        (minOf(from.first, to.first)..maxOf(from.first, to.first)).map { x ->
                            Occupied(x, from.second)
                        }
                    }

                }
            }.associateBy {
                Coordinate(it.x, it.y)
            }

    private val bottomY = input.maxOf { it.key.second }
    val realBottom = bottomY + 2

    private fun drop(loc: Coordinate, coords: Map<Coordinate, Occupied>) : Coordinate? {
        return when {
            loc.second > bottomY -> null
            coords[Coordinate(loc.first, loc.second + 1)] == null -> {
                drop(Coordinate(loc.first, loc.second + 1), coords)
            }
            coords[Coordinate(loc.first - 1 , loc.second + 1)] == null -> {
                drop(Coordinate(loc.first - 1, loc.second + 1), coords)
            }
            coords[Coordinate(loc.first + 1 , loc.second + 1)] == null -> {
                drop(Coordinate(loc.first + 1, loc.second + 1), coords)
            }
            else -> {
                loc
            }
        }
    }

    private fun drop2(loc: Coordinate, coords: Map<Coordinate, Occupied>) : Coordinate {
        return when {
            loc.second + 1 == realBottom -> loc
            coords[Coordinate(loc.first, loc.second + 1)] == null -> {
                drop2(Coordinate(loc.first, loc.second + 1), coords)
            }
            coords[Coordinate(loc.first - 1 , loc.second + 1)] == null -> {
                drop2(Coordinate(loc.first - 1, loc.second + 1), coords)
            }
            coords[Coordinate(loc.first + 1 , loc.second + 1)] == null -> {
                drop2(Coordinate(loc.first + 1, loc.second + 1), coords)
            }
            else -> {
                loc
            }
        }
    }

    fun part1(): String {
        val startPosition = Coordinate(500, 0)
        val simulation = generateSequence( input) {
            val endPosition = drop(startPosition, it)
            if(endPosition != null) it + (endPosition to Occupied(endPosition.first, endPosition.second))
            else null
        }
        simulation.takeWhile { it != null }
        return "Simulation run ${simulation.count() - 1}"
    }

    fun part2(): String {
        val perf = Helpers.PerformanceTime()
        val startPosition = Coordinate(500, 0)
        val simulation = generateSequence(input.toMutableMap()) {
            val endPosition = drop2(startPosition, it)
            if(!it.containsKey(endPosition)) {
                it[endPosition] = Occupied(endPosition.first, endPosition.second)
                it
            } else null

        }
        simulation.takeWhile { it != null }

        val str = "Simulation run ${simulation.count() - 1}"
        perf.time()
        return str
    }
}
fun main(args: Array<String>) {
    val d = Day14()
    //println(d.part1())
    println(d.part2())
}