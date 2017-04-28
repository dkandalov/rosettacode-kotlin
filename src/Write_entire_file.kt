package `write_entire_file`

// version 1.1.1

import java.io.File

fun main(args: Array<String>) {
    val text = "empty vessels make most noise"
    File("output.txt").writeText(text)
}