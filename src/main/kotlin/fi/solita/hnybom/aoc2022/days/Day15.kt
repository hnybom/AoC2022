package fi.solita.hnybom.aoc2022.days

import fi.solita.hnybom.aoc2022.utils.Helpers
import java.io.File
import kotlin.math.*

class Day15 {

    data class Area(val max: Coordinate, val min: Coordinate)

    private val lineRegex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input15.txt")
            .readLines()
            .map {
                val rg = lineRegex.find(it)
                val (sx, sy, bx, by) = rg!!.destructured
                Coordinate(sx.toInt(), sy.toInt()) to Coordinate(bx.toInt(), by.toInt())
            }

    private val beaconLocations = input.map { it.second }.associateBy { it }

    private val impossibleDistances = run {
        val radius = input.map { sensorAndBeacon ->
            val xDif = abs(sensorAndBeacon.first.first - sensorAndBeacon.second.first)
            val yDif = abs(sensorAndBeacon.first.second - sensorAndBeacon.second.second)
            val distance = xDif + yDif
            sensorAndBeacon.first to distance
        }
        radius.toMap()
    }

    private fun calculateImpossibleLocations(beacons: Map<Coordinate, Int>, row: Int) : HashSet<Int> {
        return beacons.flatMapTo(HashSet()) { sensorAndBeacon ->
            val beacon = sensorAndBeacon.key
            val distance = sensorAndBeacon.value
            val rowsDistance = abs(distance - abs(beacon.second - row))
            (-rowsDistance..rowsDistance).map { it + beacon.first }
        }
    }

    //coordinates.column + abs(beaconDistance - abs(row - coordinates.row))
    private fun maxRowCovered(beacons: Map<Coordinate, Int>, row: Int) : Int? {
        return beacons.map { sensorAndBeacon ->
            val beacon = sensorAndBeacon.key
            val distance = sensorAndBeacon.value
            val rowsDistance = abs(distance - abs(beacon.second - row))
            beacon.first + rowsDistance
        }.maxOrNull()
    }

    private fun filterInRangeSensors(y: Int) = impossibleDistances.filter {
        val range = (it.key.second - it.value)..(it.key.second + it.value)
        range.contains(y)
    }

    private fun isCovered(sensor: Coordinate, point: Coordinate, distance: Int): Boolean {
        return abs(sensor.first - point.first) + abs(sensor.second - point.second) <= distance
    }

    fun filterCoveredSensors(point: Coordinate): Map<Coordinate, Int> {
        return impossibleDistances.filter {
            isCovered(it.key, point, it.value)
        }
    }

    fun part1(): String {
        val y = 10
        val inDistance = filterInRangeSensors(y)
        val rowXs = calculateImpossibleLocations(inDistance, y)
        val counts = rowXs.count { !beaconLocations.containsKey(it to y) }
        return "Covered at row $counts"
    }

    fun part2(): String {
        val p = Helpers.PerformanceTime()
        val rangeTop = 4000000
        (0..rangeTop).forEach { y ->
            var x = 0
            while(x <= rangeTop) {
                val maxCovered = maxRowCovered(filterCoveredSensors(Coordinate(x, y)), y)
                if(maxCovered == null) {
                    p.time()
                    return "Part 2 result: ${(x.toLong() * 4000000L) + y.toLong()}"
                }
                else if(x < maxCovered) x = maxCovered
                else x++
            }
        }
        return ""
    }
}

fun main(args: Array<String>) {
    val d = Day15()
    println(d.part1())
    println(d.part2())
}