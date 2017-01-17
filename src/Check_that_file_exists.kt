package `check_that_file_exists`

// version 1.0.6

import java.io.File

fun main(args: Array<String>) {
    val filePaths = arrayOf("input.txt", "c:\\input.txt", "zero_length.txt", "`Abdu'l-Bahá.txt")
    val dirPaths  = arrayOf("docs", "c:\\docs")
    for (filePath in filePaths) {
        var f = File(filePath) 
        println("$filePath ${if (f.exists() && !f.isDirectory()) "exists" else "does not exist"}")
    }
    for (dirPath in dirPaths) {
        var d = File(dirPath) 
        println("$dirPath ${if (d.exists() && d.isDirectory()) "exists" else "does not exist"}")
    }
}