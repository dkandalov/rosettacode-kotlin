package `integer_sequence`

// version 1.0.5-2

import java.math.BigInteger

fun main(args: Array<String>) {
    var bi = BigInteger.ZERO
    val limit = BigInteger.valueOf(100000) // stop after 100,000 iterations, say
    while(true) {
        bi = bi + BigInteger.ONE
        println(bi)
        if (bi.compareTo(limit) == 0) return     
    }
}