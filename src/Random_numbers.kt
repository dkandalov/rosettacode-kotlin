package `random_numbers`

// version 1.0.6

import java.util.Random

fun main(args: Array<String>) {
    val r = Random()
    val da = DoubleArray(1000)
    for (i in 0 until 1000)  da[i] = 1.0 + 0.5 * r.nextGaussian()
    // now check actual mean and SD
    val mean = da.sum() / 1000.0
    val sd = Math.sqrt(da.map { (it - mean) * (it - mean) }.sum() / 1000.0)
    println("Mean is $mean")
    println("S.D. is $sd")
}