package `hickerson_series_of_almost_integers`

// version 1.0.6

import java.math.*

object Hickerson {
    private val ln2 = "0.693147180559945309417232121458" 

    fun almostInteger(n: Int): Boolean {
        val a = BigDecimal(ln2).pow(n + 1) * BigDecimal(2)
        var nn = n
        var f = n.toLong() 
        while (--nn > 1) f *= nn
        val b = BigDecimal(f).divide(a, MathContext.DECIMAL128)
        val c = b.movePointRight(1).toBigInteger() % BigInteger.TEN
        return c.toString().matches(Regex("0|9"))
    }
}

fun main(args: Array<String>) {
    for (n in 1..17) println("${"%2d".format(n)} is almost integer: ${Hickerson.almostInteger(n)}")
}