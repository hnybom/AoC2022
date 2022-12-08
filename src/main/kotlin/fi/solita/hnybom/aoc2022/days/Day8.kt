package fi.solita.hnybom.aoc2022.days

import java.io.File
class Day8 {

    data class Coordinate(val x: Int, val y: Int)
    data class Tree(val coords: Coordinate, val height: Int)

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input8.txt")
            .readLines()
            .flatMapIndexed {
                y, str -> str.toCharArray()
                    .mapIndexed { x, c -> Coordinate(x, y) to Tree(Coordinate(x, y),c.digitToInt()) }
            }.toMap()

    val maxX = input.maxBy { it.component2().coords.x }.component2().coords.x
    val maxY = input.maxBy { it.component2().coords.y }.component2().coords.y

    private fun isTreeInvisible(p: Tree): Boolean {

        if(p.coords.x == 0 || p.coords.x == maxX || p.coords.y == 0 || p.coords.y == maxY) return false

        val columnsUp = (0..p.coords.x).filter { it != p.coords.x }
        val columnsDown = (p.coords.x..maxX).filter { it != p.coords.x }
        val rowsLeft = (0..p.coords.y).filter { it != p.coords.y }
        val rowsRight = (p.coords.y..maxY).filter { it != p.coords.y }

        val invisibleFromSides = columnsUp.any { x ->
            input[Coordinate(x, p.coords.y)]!!.height >= p.height
        } && columnsDown.any { x ->
            input[Coordinate(x, p.coords.y)]!!.height >= p.height
        }

        val invisibleFromTopOrBottom = rowsLeft.any { y ->
            input[Coordinate(p.coords.x, y)]!!.height >= p.height
        } && rowsRight.any { y ->
            input[Coordinate(p.coords.x, y)]!!.height >= p.height
        }

        return invisibleFromSides && invisibleFromTopOrBottom
    }

    fun calculateScenicScoreForTree(p: Tree): Int {
        if(p.coords.x == 0 || p.coords.x == maxX || p.coords.y == 0 || p.coords.y == maxY) return 0

        val columnsLeft = (0..p.coords.x).reversed().filter { it != p.coords.x }
        val columnsRight = (p.coords.x..maxX).filter { it != p.coords.x }
        val rowsUp = (0..p.coords.y).reversed().filter { it != p.coords.y }
        val rowsDown = (p.coords.y..maxY).filter { it != p.coords.y }

        val count1 = filterPath(columnsLeft, p) {
            Coordinate(it, p.coords.y)
        }

        val count2 = filterPath(columnsRight, p) {
            Coordinate(it, p.coords.y)
        }

        val count3 = filterPath(rowsUp, p) {
                Coordinate(p.coords.x, it)
        }

        val count4 = filterPath(rowsDown, p) {
            Coordinate(p.coords.x, it)
        }

        return count1.first * count2.first * count3.first * count4.first
    }

    private fun filterPath(
        path: List<Int>,
        p: Tree,
        getCoordinate: (Int) -> Coordinate
    ) = path.fold(0 to true) { acc, x ->
        if (acc.second && input[getCoordinate(x)]!!.height < p.height) {
            acc.first + 1 to true
        } else {
            if (acc.second) acc.first + 1 to false
            else acc.first to false
        }
    }

    fun part1(): String {
        val visbleTrees = input.values.filter {
            !isTreeInvisible(it)
        }
        return "Invisible trees count ${visbleTrees.size}"
    }

    fun part2(): String {
        val treesWithScore = input.values.map {
            it to calculateScenicScoreForTree(it)
        }

        val bestTree = treesWithScore.maxBy { it.second }

        return "Best scenic score: ${bestTree}"
    }
}
fun main(args: Array<String>) {
    val d = Day8()
    println(d.part1())
    println(d.part2())
}