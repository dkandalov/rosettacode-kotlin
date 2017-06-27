package `dutch_national_flag_problem`

// version 1.1.2

import java.util.Random

const val NUM_BALLS = 9

val colorMap = mapOf(0 to "RED", 1 to "WHITE", 2 to "BLUE")

fun main(args: Array<String>) {
    val r = Random()
    val balls = IntArray(NUM_BALLS)
    balls[0] = 1 + r.nextInt(2) // ensures first ball is 1 or 2 and so can't be RED
    for (i in 1 until NUM_BALLS) balls[i] = r.nextInt(3)
    // print the colors of the balls before sorting
    println("Before sorting : ${balls.map { colorMap[it] }}")
    // sort the balls in natural order
    balls.sort()
    // print the colors of the balls after sorting
    println("After sorting  : ${balls.map { colorMap[it] }}")
}