package `sum_of_a_series`

// version 1.0.6

fun main(args: Array<String>) {
    val n = 1000
    var sum = 0.0
    for (k in 1..n) sum += 1.0 / (k * k)
    println("Actual sum is $sum")
    println("zeta(2)    is ${Math.PI * Math.PI / 6.0}")
}