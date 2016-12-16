package `loops_nested`

import java.util.Random

fun main(args: Array<String>) {
    val r = Random()
    val a = Array(10) { IntArray(10) { r.nextInt(20) + 1 } }
    println("array:")
    for (i in a.indices) println("row $i: " + a[i].asList())

    println("search:")
    Outer@ for (i in a.indices) {
        print("row $i: ")
        for (j in a[i].indices) {
            print(" " + a[i][j])
            if (a[i][j] == 20) break@Outer
        }
        println()
    }
    println()
}