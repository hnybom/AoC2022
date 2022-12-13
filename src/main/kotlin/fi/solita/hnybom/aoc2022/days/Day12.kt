package fi.solita.hnybom.aoc2022.days

import java.io.File
typealias Coordinate = Pair<Int, Int>
class Day12 {

    data class Node(val x: Int, val y: Int, val height: Char, val paths: MutableList<Node>, val cost: Int = 1) {

        fun getProcessedHeight(): Char {
            return when(height) {
                'S' -> 'a'
                'E' -> 'z'
                else -> height
            }
        }
        override fun equals(other: Any?) : Boolean {
            if(other == null) return false
            if(other !is Node) return false
            return EssentialData(this) == EssentialData(other)
        }
        override fun hashCode() = EssentialData(this).hashCode()
        override fun toString() = EssentialData(this).toString()
    }

    private data class EssentialData(val x: Int, val y: Int, val height: Char) {
        constructor(node: Node) : this(x = node.x, y = node.y, height = node.height)
    }

    private fun getAdjacent(p: Node, map: Map<Coordinate, Node>): List<Node> {
        return listOfNotNull(
            map.getOrDefault(Coordinate(p.x - 1, p.y), null),
            map.getOrDefault(Coordinate(p.x + 1, p.y), null),
            map.getOrDefault(Coordinate(p.x, p.y + 1), null),
            map.getOrDefault(Coordinate(p.x, p.y - 1), null)
        )
    }

    private val nodes =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input12.txt")
            .readLines()
            .flatMapIndexed { y, s -> s.mapIndexed { x, height ->
                Coordinate(x, y) to Node(x, y, height, mutableListOf())
            } }.toMap()

    private val start: Node = nodes.values.find { it.height == 'S' }!!
    private val target: Node = nodes.values.find { it.height == 'E' }!!

    init {
        nodes.values.forEach { from ->
            from.paths.addAll(getAdjacent(from, nodes).filter { to ->
                from.getProcessedHeight().code - to.getProcessedHeight().code >= -1
            })
        }
    }

    private fun dijkstra(map: Map<Coordinate, Node>, start: Node, end : Node) : Long {
        val costs = map.values.associateWith { Long.MAX_VALUE }.toMutableMap()
        val route = map.values.associateWith<Node, Node?> { null }.toMutableMap()

        costs[start] = 0
        val q = mutableListOf(start)

        while (q.isNotEmpty()) {
            val u = q.minByOrNull { costs[it]!! }!!
            q.remove(u)
            u.paths.forEach { v ->
                val alt = costs[u]!! + v.cost
                if (alt < costs[v]!!) {
                    costs[v] = alt
                    route[v] = u
                    if (v == end) {
                        return alt
                    }
                    q.add(v)
                }
            }
        }
        return 0L
    }

    fun part1(): String {
        val shortest = dijkstra(nodes, start, target)
        return "Shortest path $shortest"
    }

    fun part2(): String {
        val now = System.currentTimeMillis()
        val lowestPoints = nodes.values.filter { it.getProcessedHeight() == 'a' }
        val paths = lowestPoints.map { dijkstra(nodes, it, target) }.filter { it != 0L }
        val shortest = paths.minBy { it }
        return "Shortest path from any a $shortest in ${System.currentTimeMillis() - now}ms"
    }
}
fun main(args: Array<String>) {
    val d = Day12()
    println(d.part1())
    println(d.part2())
}