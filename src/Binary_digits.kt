package `binary_digits`

// version 1.0.5-2

fun main(args: Array<String>) {
    val numbers = intArrayOf(5, 50, 9000)
    for (number in numbers) println("%4d".format(number) + " -> " + Integer.toBinaryString(number))
}