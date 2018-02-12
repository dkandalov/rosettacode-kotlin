package `evaluate_binomial_coefficients`

// version 1.0.5-2

fun factorial(n: Int) = when {
    n < 0 -> throw IllegalArgumentException("negative numbers not allowed")
    else  -> {
        var ans = 1L
        for (i in 2..n) ans *= i
        ans
    }
}

fun binomial(n: Int, k: Int) = when {
    n < 0 || k < 0 -> throw IllegalArgumentException("negative numbers not allowed")
    n == k         -> 1L
    else           -> {
        var ans = 1L
        for (i in n - k + 1..n) ans *= i
        ans / factorial(k)
    }
}

fun main(args: Array<String>) {
    for (n in 0..14) {
        for (k in 0..n)
            print("%4d ".format(binomial(n, k)))
        println()
    }
}