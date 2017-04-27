package `read_a_file_character_by_character_utf8`

// version 1.1.1

import java.io.File

const val EOF = -1

fun main(args: Array<String>) {
    val reader = File("input.txt").reader()  // uses UTF-8 by default
    reader.use {
        while (true) {
            val c = reader.read()
            if (c == EOF) break
            print(c.toChar()) // echo to console
        }
    }
}