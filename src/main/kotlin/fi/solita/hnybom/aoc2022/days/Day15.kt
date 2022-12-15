package fi.solita.hnybom.aoc2022.days

import java.io.File
import kotlin.math.*

class Day15 {

    private val lineRegex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input15_test.txt")
            .readLines()
            .map {
                val rg = lineRegex.find(it)
                val (sx, sy, bx, by) = rg!!.destructured
                Coordinate(sx.toInt(), sy.toInt()) to Coordinate(bx.toInt(), by.toInt())
            }

    val beaconLocations = input.map { it.second }.associateBy { it }

    private val impossibleDistances = run {
        val radius = input.map { sensorAndBeacon ->
            val xDif = abs(sensorAndBeacon.first.first - sensorAndBeacon.second.first)
            val yDif = abs(sensorAndBeacon.first.second - sensorAndBeacon.second.second)
            val distance = xDif + yDif

            sensorAndBeacon.first to distance
        }
        radius.toMap()
    }

    fun calculateImpossibleLocations(beacons: Map<Coordinate, Int>, row: Int) : List<IntRange> {
        val radius = beacons.map { sensorAndBeacon ->
            val beacon = sensorAndBeacon.key
            val distance = sensorAndBeacon.value
            val rowsDistance = abs(distance - abs(beacon.second - row))
            val rangePos = (-rowsDistance..rowsDistance)
            rangePos
            /*val rd = rangePos.map { x: Int ->
                Coordinate(beacon.first + x, row)
            }
            rd*/
        }
        return radius
    }

    fun part1(): String {
        val y = 10

        val inDistance = impossibleDistances.filter {
            it.key.second + it.value >= y
        }

        val atRow = calculateImpossibleLocations(inDistance, y)
        val minX = atRow.minOfOrNull {
            it.min()
        }
        val maxX = atRow.maxOfOrNull {
            it.max()
        }
        //val counts = atRow.filter { !beaconLocations.containsKey(it to y) }
        val counts = (minX!!..maxX!!).count()
        return "Covered at row $counts"
    }

    fun part2(): String {

        return ""
    }
}
fun main(args: Array<String>) {
    val d = Day15()
    println(d.part1())
    println(d.part2())
}