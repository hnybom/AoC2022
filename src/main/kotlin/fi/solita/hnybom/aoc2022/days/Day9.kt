package fi.solita.hnybom.aoc2022.days

import java.io.File
import java.lang.IllegalArgumentException
import kotlin.math.abs

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
    data class StepResult(val head: Coordinate, val tail: Coordinate)

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input9.txt")
            .readLines().map {
                val split = it.split(" ")
                Movement(DIRECTION.fromChar(split[0][0]), split[1].toInt())
            }

    private fun isNotAdjacent(coord1: Coordinate, coord2: Coordinate) =
        abs(coord1.x - coord2.x) > 1 || abs(coord1.y - coord2.y) > 1
    private fun follow(head: Coordinate, tail: Coordinate) : Coordinate {
        return if(isNotAdjacent(head, tail)) {
            Coordinate(
                if(head.x == tail.x) tail.x
                else if(head.x > tail.x) {
                    tail.x + 1
                } else tail.x - 1,
                if(head.y == tail.y) tail.y
                else if(head.y > tail.y) {
                    tail.y + 1
                } else tail.y - 1
            )
        } else tail
    }

    private fun move(coord : Coordinate, dir: DIRECTION) : Coordinate {
        return when (dir) {
            DIRECTION.UP -> Coordinate(coord.x, coord.y + 1)
            DIRECTION.RIGHT -> Coordinate(coord.x + 1, coord.y)
            DIRECTION.DOWN -> Coordinate(coord.x, coord.y - 1)
            DIRECTION.LEFT -> Coordinate(coord.x - 1, coord.y)
        }
    }

    private fun processStep(headLocation: Coordinate,
                    tailLocation: Coordinate,
                    stepsLeft: Int,
                    d: DIRECTION) : List<StepResult> {

        if(stepsLeft == 0) return emptyList()
        val newHeadCoord = move(headLocation, d)
        val newTailLocationMaybe = follow(newHeadCoord, tailLocation)
        return if(newTailLocationMaybe == null) {
            listOf(StepResult(newHeadCoord, tailLocation)) + processStep(newHeadCoord, tailLocation, stepsLeft - 1, d)
        } else listOf(StepResult(newHeadCoord, newTailLocationMaybe)) + processStep(newHeadCoord, newTailLocationMaybe, stepsLeft - 1, d)
    }

    private fun simulateMoves1(moves: List<Movement>,
                      headLocation: Coordinate = Coordinate(0, 0),
                      tailLocation: Coordinate = Coordinate(0, 0)) : List<StepResult> {

        if(moves.isEmpty()) return emptyList()
        val movement = moves.first()
        val steps = processStep(headLocation, tailLocation, movement.amount, movement.d)
        return steps + simulateMoves1(moves.drop(1), steps.last().head, steps.last().tail)
    }

    private fun processKnots(knots: List<Coordinate>, d: DIRECTION) : List<StepResult> {
        if(knots.size < 2) return emptyList()
        val followerNewLocationMaybe = follow(knots[0], knots[1])
        return if(followerNewLocationMaybe == null) listOf(StepResult(knots[0], knots[1])) + processKnots(knots.drop(1), d)
        else  {
            val nextStepKnots = listOf(followerNewLocationMaybe) + knots.drop(2)
            listOf(
                StepResult(knots[0], followerNewLocationMaybe)
            ) + processKnots(nextStepKnots, d)
        }
    }

    private fun processStep2(rope: List<Coordinate>,
                            stepsLeft: Int,
                            d: DIRECTION) : List<List<StepResult>> {

        if(stepsLeft == 0) return emptyList()
        val newHeadCoord = move(rope.first(), d)
        val stepSteps = processKnots(listOf(newHeadCoord) + rope.drop(1), d)
        val newRope = stepSteps.map {
            it.head
        } + stepSteps.last().tail

        return listOf(stepSteps) + processStep2(newRope, stepsLeft - 1, d)
    }

    private fun simulateMoves2(moves: List<Movement>,
                               rope: List<Coordinate>) : List<List<StepResult>> {
        if(moves.isEmpty()) return emptyList()
        val movement = moves.first()
        val steps = processStep2(rope, movement.amount, movement.d)
        val newRope = steps.last().map {
            it.head
        } + steps.last().last().tail

        return steps + simulateMoves2(moves.drop(1), newRope)
    }

    fun part1(): String {
        val allSteps = simulateMoves1(input)
        val tailDistinctCoordinates = allSteps.map { it.tail }.distinct().count()
        return "Tail distinct coordinates: $tailDistinctCoordinates"
    }

    fun part2(): String {
        val allSteps = simulateMoves2(input, (0..9).map { Coordinate(0, 0) })
        val tailMoves = allSteps.map { it.last() }.map { it.tail } + allSteps.last().last().tail
        val tailDistinctCoordinates = tailMoves.distinct().count()
        return "Long tail distinct coordinates: $tailDistinctCoordinates"
    }
}
fun main(args: Array<String>) {
    val d = Day9()
    println(d.part1())
    println(d.part2())
}