package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day13 {

    interface ListEntity {
        val parent: ListEntity?
    }
    data class XList(val entities: MutableList<ListEntity>, override val parent: ListEntity?) : ListEntity {
        override fun equals(other: Any?) : Boolean {
            if(other == null) return false
            if(other !is XList) return false
            return EssentialDataList(this) == EssentialDataList(other)
        }
        override fun hashCode() = EssentialDataList(this).hashCode()
        override fun toString() = EssentialDataList(this).toString()
    }

    data class XInt(val value: Int, override val parent: ListEntity?) : ListEntity {
        override fun equals(other: Any?) : Boolean {
            if(other == null) return false
            if(other !is XInt) return false
            return EssentialDataInt(this) == EssentialDataInt(other)
        }
        override fun hashCode() = EssentialDataInt(this).hashCode()
        override fun toString() = EssentialDataInt(this).toString()
    }

    private data class EssentialDataInt(val value: Int) {
        constructor(num: XInt) : this(value = num.value)
    }
    private data class EssentialDataList(val entities: MutableList<ListEntity>,) {
        constructor(list: XList) : this(entities = list.entities)
    }

    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input13_test.txt")
            .readLines()
            .filter { it.isNotEmpty() }
            .flatMap { parse(it, null) }
            .chunked(2)

    // Horrible horrible code!
    private fun parse(str: String, parent: ListEntity?): List<ListEntity> {
        if(str.isEmpty()) return emptyList()
        val c = str.first()
        return if(c == '[') {
            var openCount = 0
            var listContents = str.takeWhile {
                if(it == '[') openCount++
                else if(it == ']') openCount--
                openCount != 0
            }
            val current = XList(ArrayList(), parent)
            val children = parse(listContents.drop(1), current)
            current.entities.addAll(children)
            listOf(current)
        } else if(c.isDigit()) {
            val split = str.split(",")
            val start = split.takeWhile { !it.contains('[') }
            val end = split.takeLastWhile { !it.contains(']') }
            val numbers = if(start != end) start + end else start
            val numbersList = numbers.map {
                XInt(it.toInt(), parent)
            }

            val startDropped = str.drop(start.sumOf { it.length } + start.size)
            val left = startDropped.dropLast(end.sumOf { it.length } + end.size)
            numbersList + if (left.isNotEmpty()) parse(left, parent) else emptyList()

        } else if(c == ']') emptyList()
        else {
            throw IllegalArgumentException("Unexpected char: $c")
        }
    }

    fun part1(): String {
        input.map {
            val first = it[0]
            val second = it[1]
            if(first is XInt && second is XInt) first.value > 
        }
        return ""
    }

    fun part2(): String {

        return ""
    }
}
fun main(args: Array<String>) {
    val d = Day13()
    println(d.part1())
    println(d.part2())
}