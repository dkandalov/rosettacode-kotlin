package scripts

import java.io.File

fun main(args: Array<String>) {
    val cacheDir = ".cache"
    println(">>> Deleting files from $cacheDir")
    File(cacheDir).listFiles().forEach {
        val wasDeleted = it.delete()
        if (wasDeleted) {
            println("Deleted ${it.name}")
        } else {
            println("Failed to delete ${it.name}")
        }
    }
}