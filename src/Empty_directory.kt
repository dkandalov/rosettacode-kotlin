package `empty_directory`

// version 1.0.6

import java.io.File

fun main(args: Array<String>) {
    val dirPath = "docs" // or whatever
    val isEmpty = (File(dirPath).list().size == 0)
    println("$dirPath is ${if (isEmpty) "empty" else "not empty"}")
}