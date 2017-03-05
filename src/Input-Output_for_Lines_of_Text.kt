package `input_output_for_lines_of_text`

// version 1.1.0

fun output(lines: Array<String>) = println(lines.joinToString("\n"))

fun main(args: Array<String>) {
    println("Enter the number of lines to be input followed by those lines:\n")
    val n = readLine()!!.toInt()
    val lines = Array<String>(n) { readLine()!! }
    println("\nThe lines you entered are:\n")
    output(lines)
}