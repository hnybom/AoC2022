package fi.solita.hnybom.aoc2022.days

import java.io.File
import java.lang.IllegalStateException

class Day7 {

    interface FileSystemNode {
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
        File("/home/henriny/work/own/AoC2022/src/main/resources/input7.txt")
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
                    val existingDir = currentDir.children.find { it.name == dirName }
                    val subDir = if(existingDir != null) {
                        existingDir
                    } else {
                        val newDir = Dir(dirName, currentDir, emptyList())
                        currentDir.children = currentDir.children + newDir
                        newDir
                    }
                    crawl(commands.drop(1), subDir as Dir)
                }
            }
        } else if(command.startsWith("$ ls")) {
            val listing = commands.drop(1).takeWhile {
                !it.startsWith("$")
            }

            val filesAndDirs = listing.map {
                val listing = it.split(" ")
                if(listing[0] == "dir") {
                    Dir(listing[1], currentDir, emptyList())
                } else {
                    XFile(listing[1], listing[0].toInt(), currentDir)
                }
            }
            currentDir.children = currentDir.children + filesAndDirs
            crawl(commands.drop(1 + filesAndDirs.size), currentDir)
        }
    }

    private fun calculateTreeSize(currentDir: Dir) : Long {
        currentDir.totalSize = currentDir.children.sumOf {
            when (it) {
                is Dir -> calculateTreeSize(it)
                is XFile -> it.size.toLong()
                else -> throw IllegalStateException()
            }
        }
        return currentDir.totalSize
    }

    init {
        crawl(input, root)
        calculateTreeSize(root)
    }

    private fun filterTreeByTotalSize(currentDir: Dir, acc: List<Dir>, filter: (Long) -> Boolean): List<Dir> {
        val childDirs = currentDir.children.flatMap {
            when (it) {
                is Dir -> filterTreeByTotalSize(it, acc, filter)
                else -> emptyList()
            }
        }

        val newAcc = if(filter(currentDir.totalSize)) {
            acc + currentDir
        } else acc

        return childDirs + newAcc
    }

    fun part1(): String {
        val underMax = filterTreeByTotalSize( root, emptyList()) { size ->
            size <= 100000
        }
        val totalSum = underMax.sumOf { it.totalSize }
        return "Total sum of dirs: $totalSum"
    }

    fun part2(): String {
        val totalSizeOfFS = 70000000L
        val neededSpace = 30000000L

        val sizeToDelete = neededSpace - (totalSizeOfFS - root.totalSize)
        val candidates = filterTreeByTotalSize(root, emptyList()) { size ->
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