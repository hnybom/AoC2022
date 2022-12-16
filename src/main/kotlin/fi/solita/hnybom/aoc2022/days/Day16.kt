package fi.solita.hnybom.aoc2022.days

import java.io.File
class Day16 {

    val lineRegex = "^Valve (\\D+) has flow rate=(\\d+); tunnels? leads? to valves? (\\D+)\$".toRegex()
    data class Valve(val id: String, val flow: Int, val paths: MutableList<Valve>, val pathIds: List<String>) {
        override fun hashCode() = EssentialDataInt(this).hashCode()
        override fun toString() = EssentialDataInt(this).toString()
    }

    data class TraveledNode(val valve: Valve, val opened: Boolean, val timeLeft: Int)

    private data class EssentialDataInt(val id: String, val flow: Int, val pathIds: List<String>) {
        constructor(v: Valve) : this(id = v.id, flow = v.flow, pathIds = v.pathIds)
    }

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input16_test.txt")
            .readLines()
            .map {
                val rg = lineRegex.find(it)
                val (id, flow, pathStr) = rg!!.destructured
                Valve(id, flow.toInt(), ArrayList(), pathStr.split(",").map { it.trim() })
            }.associateBy { it.id }

    private val priorityList = input.values.sortedByDescending { it.flow }

    init {
        input.values.forEach {valve ->
            valve.paths.addAll(valve.pathIds.map { input[it]!! })
        }
    }

    private val start = input["AA"]!!

    private fun dijkstra(map: Map<Coordinate, Valve>, start: Valve, end : Valve) : Long {
        val costs = map.values.associateWith { Long.MAX_VALUE }.toMutableMap()
        val route = map.values.associateWith<Valve, Valve?> { null }.toMutableMap()

        costs[start] = 0
        val q = mutableListOf(start)

        while (q.isNotEmpty()) {
            val u = q.minByOrNull { costs[it]!! }!!
            q.remove(u)
            u.paths.forEach { v ->
                val alt = costs[u]!! + 1
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

    private fun travel(currentValve: Valve, path: List<TraveledNode>, time: Int): List<List<TraveledNode>> {
        val node = if(time > 0 && currentValve.flow > 0 && path.find { it.valve == currentValve }?.opened != true) {
            TraveledNode(currentValve, true, time - 1)
        }
        else TraveledNode(currentValve, false, time )

        if(node.timeLeft == 0) {
            return listOf(path + node)
        }

        val travels = currentValve.paths.flatMap { valve ->
            if(path.find { it.valve == valve }?.opened != true) travel(valve, path + node, time - 1)
            else emptyList()
        }

        return travels.ifEmpty { return listOf(path + node) }

    }

    fun part1(): String {
        val paths = travel(start, emptyList(), 30)
        val maxFlows = paths.map {path ->
            val sumOfPath = path.map {
                if (it.opened) it.valve.flow * it.timeLeft
                else 0
            }.sum()
            path to sumOfPath
        }.sortedByDescending { it.second }
        return "Max flow: ${maxFlows.maxOf { it.second }}"
    }

    fun part2(): String {

        return ""
    }
}
fun main(args: Array<String>) {
    val d = Day16()
    println(d.part1())
    println(d.part2())
}