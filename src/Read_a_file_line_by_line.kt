package `read_a_file_line_by_line`

// version 1.1.1

import java.io.File

fun main(args: Array<String>) {
    File("input.txt").forEachLine { println(it) }
}