package `empty_directory`

// version 1.1

import java.io.File

fun main(args: Array<String>) {
    val dirPath = "docs" // or whatever
    val isEmpty = (File(dirPath).list().isEmpty())
    println("$dirPath is ${if (isEmpty) "empty" else "not empty"}")
}