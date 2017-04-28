package `hello_world_line_printer`

// version 1.1.1

import java.io.File

fun main(args: Array<String>) {
    val text = "Hello World!\n"
    File("/dev/lp0").writeText(text)
}