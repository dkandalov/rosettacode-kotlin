package `repeat_a_string_1`

operator fun String.times(n: Int) = this.repeat(n)

fun main(args: Array<String>) = println("ha" * 5)