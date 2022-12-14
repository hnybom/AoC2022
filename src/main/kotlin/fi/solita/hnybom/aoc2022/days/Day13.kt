package fi.solita.hnybom.aoc2022.days

import java.io.File
import java.lang.IllegalStateException

class Day13 {

    companion object {
        fun areInRightOrder(first: ListEntity?, second: ListEntity?): Boolean? {

            return when {
                (first == null && second != null) -> true
                (first != null && second == null) -> false
                (first == null && second == null) -> null
                (first is XInt && second is XInt) -> {
                    if(first.value == second.value) return null
                    else first.value < second.value
                }
                (first is XList && second is XList) -> {
                    if(first.entities.isEmpty() && second.entities.isNotEmpty()) return true
                    else if(first.entities.isNotEmpty() && second.entities.isEmpty()) return false
                    else {
                        val inOrder = (0..maxOf(first.entities.size - 1, second.entities.size - 1)).map {
                            areInRightOrder(first.entities.getOrNull(it), second.entities.getOrNull(it))
                        }
                        val firstTrue = inOrder.indexOfFirst { it == true}
                        val firstFalse = inOrder.indexOfFirst { it == false}
                        when {
                            (firstTrue == -1 && firstFalse == -1) -> null
                            (firstTrue != -1 && firstFalse == -1) -> true
                            (firstTrue == -1 && firstFalse != -1) -> false
                            else -> firstTrue < firstFalse
                        }
                    }
                }
                (first is XList && second is XInt) -> {
                    areInRightOrder(first, XList(entities = listOf(second).toMutableList(), null, ""))
                }
                (first is XInt && second is XList) -> {
                    areInRightOrder(XList(entities = listOf(first).toMutableList(), null, ""), second)
                }
                else -> throw IllegalStateException()
            }
        }
    }

    sealed interface ListEntity : Comparable<ListEntity> {
        val parent: ListEntity?
        val string: String
    }
    data class XList(val entities: MutableList<ListEntity>, override val parent: ListEntity?,
                     override val string: String) : ListEntity {
        override fun compareTo(other: ListEntity): Int {
            return when(areInRightOrder(this, other)) {
                null -> 0
                true -> -1
                false -> 1
            }
        }

        override fun equals(other: Any?): Boolean {
            return when(other) {
                is XList -> string == other.string
                else -> false
            }
        }

        override fun hashCode() = EssentialDataList(this).hashCode()
        override fun toString() = EssentialDataList(this).toString()
    }

    data class XInt(val value: Int, override val parent: ListEntity?, override val string: String) : ListEntity {

        override fun compareTo(other: ListEntity): Int {
            return when(areInRightOrder(this, other)) {
                null -> 0
                true -> -1
                false -> 1
            }
        }

        override fun equals(other: Any?): Boolean {
            return when(other) {
                is XList -> string == other.string
                else -> false
            }
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
        File("/home/henriny/work/own/AoC2022/src/main/resources/input13.txt")
            .readLines()
            .filter { it.isNotEmpty() }
            .flatMap { parse(it, null) }

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
            val current = XList(ArrayList(), parent, str)
            val children = parse(listContents.drop(1), current)
            current.entities.addAll(children)
            val left = str.drop(listContents.length + 2)
            listOf(current) + if(left.isNotEmpty()) {
                parse(left, parent)
            } else emptyList()

        } else if(c.isDigit()) {
            val split = str.split(",")
            val start = split.takeWhile { !it.contains('[') }
            val end = split.takeLastWhile { !it.contains(']') }
            val numbers = if(start != end) start + end else start
            val numbersList = numbers.map {
                XInt(it.toInt(), parent, str)
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
        val rightOrder = input.chunked(2).mapIndexedNotNull { index, listEntities ->
            val first = listEntities[0]
            val second = listEntities[1]
            if(areInRightOrder(first, second) == true) index + 1
            else null
        }
        return "The sum of indexes: ${rightOrder.sum()}"
    }

    fun part2(): String {
        val add2 = parse("[[2]]", null).first()
        val add6 = parse("[[6]]", null).first()
        val secondPartInput = input + add2 + add6
        val ordered = secondPartInput.sorted()
        val index2 = ordered.indexOfFirst { it == add2 } + 1
        val index6 = ordered.indexOfFirst { it == add6 } + 1
        return "Decoder: ${index2 * index6}"
    }
}
fun main(args: Array<String>) {
    val d = Day13()
    println(d.part1())
    println(d.part2())
}