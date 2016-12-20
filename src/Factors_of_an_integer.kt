package `factors_of_an_integer`

// version 1.0.5-2

fun printFactors(n: Int) {
    if (n < 1) return
    print("$n => ")
    for (i in 1 .. n/2) if (n % i == 0) print("$i ")
    println(n)      
}

fun main(args: Array<String>) {
    val numbers = intArrayOf(11, 21, 32, 45, 67, 96)
    for (number in numbers) printFactors(number)
}