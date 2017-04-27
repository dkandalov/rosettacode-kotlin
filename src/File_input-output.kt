package `file_input_output`

// version 1.1.1

import java.io.File

fun main(args: Array<String>) {
    val text = File("input.txt").readText()
    File("output.txt").writeText(text)
}