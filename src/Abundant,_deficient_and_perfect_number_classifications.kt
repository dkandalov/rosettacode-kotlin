package `abundant,_deficient_and_perfect_number_classifications`

// version 1.0.5-2
 
fun sumProperDivisors(n: Int): Int {
    if (n < 2) return 0
    return (1..n/2).filter{ (n % it) == 0 }.sum()
}

fun main(args: Array<String>) {
    var sum:       Int
    var deficient: Int = 0
    var perfect:   Int = 0
    var abundant:  Int = 0
 
    for (n in 1..20000) {
        sum = sumProperDivisors(n)
        when {
            sum < n  -> deficient++
            sum == n -> perfect++
            sum > n  -> abundant++
        }
    }

    println("The classification of the numbers from 1 to 20,000 is as follows:\n")
    println("Deficient = $deficient")
    println("Perfect   = $perfect")
    println("Abundant  = $abundant")
}