package `sum_of_squares`

// version 1.0.6

fun main(args: Array<String>) {
    val vector = doubleArrayOf(3.1, 1.0, 4.0, 1.0, 5.0, 9.0)
    println(vector.map { it * it }.sum())
    val vector2 = doubleArrayOf() // empty vector
    println(vector2.map { it * it }.sum())
}