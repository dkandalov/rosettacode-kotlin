package ackermann

fun A(m: Long, n: Long): Long = when {
    m == 0L -> n + 1
    m > 0L -> when {
        n == 0L -> A(m - 1, 1)
        n > 0L -> A(m - 1, A(m, n - 1))
        else -> throw IllegalArgumentException("illegal n")
    }
    else -> throw IllegalArgumentException("illegal m")
}

fun main(args: Array<String>) {
    val M: Long = 4
    val N: Long = 20
    val r = 0..N
    for (m  in 0..M) {
        print("\nA($m, $r) =")
        var able = true
        r.forEach {
            try {
                if (able) {
                    val a = A(m, it)
                    print(" %6d".format(a))
                } else
                    print("      ?")
            } catch(e: Throwable) {
                print("      ?")
                able = false
            }
        }
    }
}