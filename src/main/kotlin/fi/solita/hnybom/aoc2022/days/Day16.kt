package fi.solita.hnybom.aoc2022.days

import fi.solita.hnybom.aoc2022.utils.Helpers
import java.io.File
class Day16 {

    val lineRegex = "^Valve (\\D+) has flow rate=(\\d+); tunnels? leads? to valves? (\\D+)\$".toRegex()
    data class Valve(val id: String, val flow: Int, val paths: MutableList<Valve>, val pathIds: List<String>) {
        override fun hashCode() = EssentialDataInt(this).hashCode()
        override fun toString() = EssentialDataInt(this).toString()
    }

    data class TraveledNode(val valve: Valve, val opened: Boolean, val timeLeft: Long, var who : String = "me")

    private data class EssentialDataInt(val id: String, val flow: Int, val pathIds: List<String>) {
        constructor(v: Valve) : this(id = v.id, flow = v.flow, pathIds = v.pathIds)
    }

    private data class DijkstraResult(val target: Valve, val timeCost: Long, val route: Map<Valve, Valve?> )

    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input16.txt")
            .readLines()
            .map {
                val rg = lineRegex.find(it)
                val (id, flow, pathStr) = rg!!.destructured
                Valve(id, flow.toInt(), ArrayList(), pathStr.split(",").map { it.trim() })
            }.associateBy { it.id }

    private val priorityList = input.values.sortedByDescending { it.flow }.filter { it.flow > 0 }

    init {
        input.values.forEach {valve ->
            valve.paths.addAll(valve.pathIds.map { input[it]!! })
        }
    }

    private val start = input["AA"]!!

    private val pathCache = HashMap<Pair<Valve, Valve>, DijkstraResult>()

    private fun dijkstra(list: Collection<Valve>, start: Valve, end : Valve) : DijkstraResult? {
        val c = pathCache[start to end]
        if(c != null) return c

        val costs = list.associateWith { Long.MAX_VALUE }.toMutableMap()
        val route = list.associateWith<Valve, Valve?> { null }.toMutableMap()

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
                        val r = DijkstraResult(v, alt, route.toMap())
                        pathCache[start to end] = r
                        return r
                    }
                    q.add(v)
                }
            }
        }
        return null
    }

    private fun optimize(currentValve: Valve, path: List<TraveledNode>, timeLeft: Long): List<List<TraveledNode>> {

        val targetCandidates = priorityList.filter { !path.map { it.valve }.contains(it) }.mapNotNull {
            dijkstra(input.values, currentValve, it)
        }

        val prioritizedCandidates = targetCandidates.map {
            val timeAtTarget = timeLeft - it.timeCost
            (timeAtTarget - 1) * it.target.flow to TraveledNode(it.target, true, timeAtTarget - 1)
        }.filter { it.first > 0 }.sortedByDescending { it.first }

        val permutations = prioritizedCandidates
            .flatMap { selectThis ->
                val newPath = path + selectThis.second
                optimize(selectThis.second.valve, newPath, selectThis.second.timeLeft)
            }

        return permutations.ifEmpty { listOf(path) }

    }

    private fun optimizeWithRonsu(currentValveForMe: Valve, currentValveForRonsu: Valve, myPath: List<TraveledNode>,
                                  ronsuPath: List<TraveledNode>, timeLeftForMe: Long, timeLeftForRonsu: Long): List<List<TraveledNode>> {

        val alreadyVisited = myPath + ronsuPath
        val alreadyVisitedValves  = alreadyVisited.map { it.valve }

        val targetCandidatesForMe = priorityList.filter { !alreadyVisitedValves.contains(it) }.mapNotNull {
            dijkstra(input.values, currentValveForMe, it)
        }

        val targetCandidatesForRonsu = priorityList.filter { !alreadyVisitedValves.contains(it) }.mapNotNull {
            dijkstra(input.values, currentValveForRonsu, it)
        }

        val prioritizedCandidatesForMe = targetCandidatesForMe.map {
            val timeAtTarget = timeLeftForMe - it.timeCost
            (timeAtTarget - 1) * it.target.flow to TraveledNode(it.target, true, timeAtTarget - 1)
        }.filter { it.first > 0 }

        val prioritizedCandidatesForRonsu = targetCandidatesForRonsu.map {
            val timeAtTarget = timeLeftForRonsu - it.timeCost
            (timeAtTarget - 1) * it.target.flow to TraveledNode(it.target, true, timeAtTarget - 1, "Ronsu")
        }.filter { it.first > 0 }

        val permutations = prioritizedCandidatesForMe
            .flatMap { selectThis ->

                prioritizedCandidatesForRonsu.filter { it.second.valve.id != selectThis.second.valve.id }.flatMap { ronsuGoHere ->

                    val myNewPath = myPath + selectThis.second
                    val ronsuNewPath = ronsuPath + ronsuGoHere.second

                    optimizeWithRonsu(selectThis.second.valve, ronsuGoHere.second.valve, myNewPath, ronsuNewPath,
                        selectThis.second.timeLeft, ronsuGoHere.second.timeLeft)
                }
            }

        val onlyRonsu = if(prioritizedCandidatesForMe.isEmpty()) {
            prioritizedCandidatesForRonsu.flatMap { ronsuGoHere ->
                val ronsuNewPath = ronsuPath + ronsuGoHere.second

                optimizeWithRonsu(currentValveForMe, ronsuGoHere.second.valve, myPath, ronsuNewPath,
                    timeLeftForMe - 1, ronsuGoHere.second.timeLeft)
            }
        } else emptyList()

        return (permutations + onlyRonsu).ifEmpty { listOf(alreadyVisited) }

    }

    fun part1(): String {
        val p = Helpers.PerformanceTime()
        val listOfPaths = optimize(start, emptyList(), 30)
        val result = listOfPaths.map { pathCanditate ->
            pathCanditate.sumOf {
                it.valve.flow * it.timeLeft
            }
        }
        p.time()
        return "Max flow: ${result.max()}"
    }

    fun part2(): String {
        val p = Helpers.PerformanceTime()
        val listOfPaths = optimizeWithRonsu(start, start, emptyList(), emptyList(), 26, 26)
        val result = listOfPaths.map { pathCanditate ->
            pathCanditate.sumOf {
                it.valve.flow * it.timeLeft
            } to pathCanditate
        }.sortedByDescending { it.first }
        p.time()
        return "Max flow: ${result.maxOf { it.first }}" 
    }
}
fun main(args: Array<String>) {
    val d = Day16()
    println(d.part1())
    println(d.part2())
}