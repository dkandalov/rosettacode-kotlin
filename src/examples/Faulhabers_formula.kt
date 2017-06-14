package `faulhabers_formula`

// version 1.1.2

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

class Frac : Comparable<Frac> {
    val num: Long
    val denom: Long

    companion object {
        val ZERO = Frac(0, 1)
        val ONE  = Frac(1, 1)
    }

    constructor(n: Long, d: Long) {
        require(d != 0L)
        var nn = n
        var dd = d
        if (nn == 0L) {
            dd = 1
        }
        else if (dd < 0) {
            nn = -nn
            dd = -dd
        }
        val g = Math.abs(gcd(nn, dd))
        if (g > 1) {
            nn /= g
            dd /= g
        }
        num = nn
        denom = dd
    }

    constructor(n: Int, d: Int) : this(n.toLong(), d.toLong())

    operator fun plus(other: Frac) =
        Frac(num * other.denom + denom * other.num, other.denom * denom)

    operator fun unaryMinus() = Frac(-num, denom)

    operator fun minus(other: Frac) = this + (-other)

    operator fun times(other: Frac) = Frac(this.num * other.num, this.denom * other.denom)

    fun abs() = if (num >= 0) this else -this

    override fun compareTo(other: Frac): Int {
        val diff = this.toDouble() - other.toDouble()
        return when {
            diff < 0.0  -> -1
            diff > 0.0  -> +1
            else        ->  0
        }
    }

    override fun equals(other: Any?): Boolean {
       if (other == null || other !is Frac) return false
       return this.compareTo(other) == 0
    }

    override fun toString() = if (denom == 1L) "$num" else "$num/$denom"

    fun toDouble() = num.toDouble() / denom
}

fun bernoulli(n: Int): Frac {
    require(n >= 0)
    val a = Array<Frac>(n + 1) { Frac.ZERO }
    for (m in 0..n) {
        a[m] = Frac(1, m + 1)
        for (j in m downTo 1) a[j - 1] = (a[j - 1] - a[j]) * Frac(j, 1)
    }
    return if (n != 1) a[0] else -a[0] // returns 'first' Bernoulli number
}

fun binomial(n: Int, k: Int): Int {
    require(n >= 0 && k >= 0 && n >= k)
    if (n == 0 || k == 0) return 1
    val num = (k + 1..n).fold(1) { acc, i -> acc * i }
    val den = (2..n - k).fold(1) { acc, i -> acc * i }
    return num / den
}

fun faulhaber(p: Int) {
    print("$p : ")
    val q = Frac(1, p + 1)
    var sign = -1
    for (j in 0..p) {
        sign *= -1
        val coeff = q * Frac(sign, 1) * Frac(binomial(p + 1, j), 1) * bernoulli(j)
        if (coeff == Frac.ZERO) continue
        if (j == 0) {
            print(when {
                coeff == Frac.ONE  -> ""
                coeff == -Frac.ONE -> "-"
                else               -> "$coeff"
            })
        }
        else {
            print(when {
                coeff == Frac.ONE  -> " + "
                coeff == -Frac.ONE -> " - "
                coeff >  Frac.ZERO -> " + $coeff"
                else               -> " - ${-coeff}"
            })
        }
        val pwr = p + 1 - j
        if (pwr > 1)
            print("n^${p + 1 - j}")
        else
            print("n")
    }
    println()
}


fun main(args: Array<String>) {
    for (i in 0..9) faulhaber(i)
}