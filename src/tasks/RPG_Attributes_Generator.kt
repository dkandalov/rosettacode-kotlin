package `rpg_attributes_generator`

// Version 1.2.51

import java.util.Random

fun main(args: Array<String>) {
    val r = Random()
    while (true) {
        val values = IntArray(6)
        for (i in 0..5) {
            val numbers = IntArray(4) { 1 + r.nextInt(6) }
            numbers.sort()
            values[i] = numbers.drop(1).sum()
        }
        val vsum = values.sum()
        val vcount = values.count { it >= 15 }
        if (vsum < 75 || vcount < 2) continue
        println("The 6 random numbers generated are:")
        println(values.asList())
        println("\nTheir sum is $vsum and $vcount of them are >= 15")
        break
    }
}