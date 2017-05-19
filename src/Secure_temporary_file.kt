package `secure_temporary_file`

// version 1.1.2

import java.io.File

fun main(args: Array<String>) {
    try {
        val tf = File.createTempFile("temp", ".tmp")
        println(tf.absolutePath)
        tf.delete()
    }
    catch (ex: Exception) {
        println(ex.message)
    }
}