package `loops_break`

import java.util.Random

fun main(args: Array<String>) {
    val rand = Random()
    while (true) {
        val a = rand.nextInt(20)
        println(a)
        if (a == 10) break
        println(rand.nextInt(20))
    }
}