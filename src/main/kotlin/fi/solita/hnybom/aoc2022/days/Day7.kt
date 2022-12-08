package fi.solita.hnybom.aoc2022.days

import java.io.File

class Day7 {

    sealed interface FileSystemNode {
        val name: String
    }

    data class Dir(
        override val name: String, val parent: Dir?,
        var children: List<FileSystemNode>, var totalSize : Long = 0) : FileSystemNode {

        override fun hashCode() = EssentialData(this).hashCode()
        override fun toString() = EssentialData(this).toString()

    }

    data class XFile(override val name: String, val size: Int, val parent: Dir) : FileSystemNode

    private data class EssentialData(val name: String, val totalSize: Long) {
        constructor(dir: Dir) : this(name = dir.name, totalSize = dir.totalSize)
    }

    private val input =
        File("/Users/hnybom/work/AoC2022/src/main/resources/input7.txt")
            .readLines()

    private val root = Dir("/", null, emptyList())

    private fun crawl(commands: List<String>, currentDir: Dir) {
        if(commands.isEmpty()) return

        val command = commands.first()
        if(command.startsWith("$ cd")) {
            when (command.substring(5)) {
                ".." -> {
                    crawl(commands.drop(1), currentDir.parent!!)
                }
                "/" -> {
                    crawl(commands.drop(1), root)
                }
                else -> {
                    val dirName = command.substring(5)
                    val newDir = Dir(dirName, currentDir, emptyList())
                    currentDir.children = currentDir.children + newDir
                    crawl(commands.drop(1), newDir)
                }
            }
        } else if(command.startsWith("$ ls")) {
            val listing = commands.drop(1).takeWhile {
                !it.startsWith("$")
            }

            val filesAndDirs = listing.mapNotNull {
                val listing = it.split(" ")
                if(listing[0] != "dir") {
                    XFile(listing[1], listing[0].toInt(), currentDir)
                } else null
            }
            currentDir.children = currentDir.children + filesAndDirs
            crawl(commands.drop(1 + listing.size), currentDir)
        }
    }

    private fun calculateTreeSize(currentDir: Dir) : Long {
        currentDir.totalSize = currentDir.children.sumOf {
            when (it) {
                is Dir -> calculateTreeSize(it)
                is XFile -> it.size.toLong()
            }
        }
        return currentDir.totalSize
    }

    init {
        crawl(input, root)
        calculateTreeSize(root)
    }

    private fun filterTreeByTotalSize(currentDir: Dir, filter: (Long) -> Boolean): List<Dir> {
        val childDirs = currentDir.children.flatMap {
            when (it) {
                is Dir -> filterTreeByTotalSize(it, filter)
                else -> emptyList()
            }
        }

        return if(filter(currentDir.totalSize)) {
            childDirs + currentDir
        } else childDirs
    }

    fun part1(): String {
        val underMax = filterTreeByTotalSize( root ) { size ->
            size <= 100000
        }
        val totalSum = underMax.sumOf { it.totalSize }
        return "Total sum of dirs: $totalSum"
    }

    fun part2(): String {
        val totalSizeOfFS = 70000000L
        val neededSpace = 30000000L

        val sizeToDelete = neededSpace - (totalSizeOfFS - root.totalSize)
        val candidates = filterTreeByTotalSize(root) { size ->
            size > sizeToDelete
        }
        val selection = candidates.minBy { it.totalSize }
        return "Selected for deletion size ${selection.totalSize}"
    }
}
fun main(args: Array<String>) {
    val d = Day7()
    println(d.part1())
    println(d.part2())
}