package `tokenize_a_string`

fun main(args: Array<String>) {
    val input = "Hello,How,Are,You,Today"
    println(input.split(',').joinToString("."))
}