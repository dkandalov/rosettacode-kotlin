package `array_concatenation_2`

fun main(args: Array<String>) {
    val a: Collection<Int> = listOf(1, 2, 3) // initialise a
    val b: Collection<Int> = listOf(4, 5, 6) // initialise b
    val c: Collection<Int> = a + b
    println(c)
}