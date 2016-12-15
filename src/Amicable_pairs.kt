package amicable_pairs

// version 1.0.5-2

fun sumProperDivisors(n: Int): Int {
    if (n < 2) return 0
    var sum = 0
    for(i in 1..n/2) {
        if ((n % i) == 0) sum += i
    }
    return sum
}

fun main(args: Array<String>) { 
    var sum = IntArray(20000, { sumProperDivisors(it) } )
    println("The pairs of amicable numbers below 20,000 are:\n")
    var m: Int
    for(n in 2..19998) {
        m = sum[n]
        if (m > n && m < 20000 && n == sum[m])
            println(n.toString().padStart(5) + " and " + m.toString().padStart(5))
    }
}