package sieve_of_eratosthenes_1

fun primesOdds(rng: Int): Iterable<Int> {
    val topi = (rng - 3) shr 1
    val lstw = topi shr 5
    val sqrtndx = (Math.sqrt(rng.toDouble()).toInt() - 3) shr 1
    val cmpsts = IntArray(lstw + 1)
    tailrec fun testloop(i: Int) {
        if (i <= sqrtndx) {
            if (cmpsts[i shr 5] and (1 shl (i and 31)) == 0) {
                val p = i + i + 3
                tailrec fun cullp(j: Int) {
                    if (j <= topi) {
                        cmpsts[j shr 5] = cmpsts[j shr 5] or (1 shl (j and 31))
                        cullp(j + p)
                    }
                }
                cullp((p * p - 3) shr 1)
            }
            testloop(i + 1)
        }
    }
    testloop(0)
    tailrec fun test(i : Int): Int {
        return if (i <= topi && cmpsts[i shr 5] and (1 shl (i and 31)) != 0) {
            test(i + 1) } else { i }
    }
    val iter = object : IntIterator() {
        var i = -1
        override fun nextInt(): Int {
            val oi = i; i = test(i + 1)
            if (oi < 0) { return 2 } else { return oi + oi + 3 } }
        override fun hasNext() = if (i < topi) { true } else { false }
    }
    return Iterable { -> iter }
}

fun main(args: Array<String>) {
    primesOdds(100).forEach { print("$it ") }
    println()
    println(primesOdds(1000000).count())
}