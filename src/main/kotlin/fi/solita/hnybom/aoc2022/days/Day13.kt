package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day13 {

    interface ListEntity {
        val parent: ListEntity?
    }
    data class XList(val entities: MutableList<ListEntity>, override val parent: ListEntity?) : ListEntity

    data class XInt(val value: Int, override val parent: ListEntity?) : ListEntity

    private val input =
        File("/home/henriny/work/own/AoC2022/src/main/resources/input13.txt")
            .readLines()
            .map { parse(it, null) }
            .zipWithNext()

    private fun parse(str: String, parent: ListEntity?): List<ListEntity> {
        if(str.isEmpty()) return emptyList()
        val c = str.first()
        return if(c == '[') {
            val list = XList(
                ArrayList(),
                parent
            )
            list.entities.addAll(parse(str.drop(1), list))
            listOf(list)
        } else if(c.isDigit()) {
            val sanitizedString = str.takeWhile { it == ']'}.trim()
            sanitizedString.split(",").map { digitStr ->
                XInt(digitStr.filter { it != ']'}.toInt(), parent)
            }
            parse(str.drop(str.length - 1) , parent)
        } else if (c == ']') emptyList()
        else {
            throw IllegalArgumentException("Unexpected char: $c")
        }
    }

    fun part1(): String {

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