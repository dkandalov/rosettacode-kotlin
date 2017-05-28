package `averages_pythagorean_means`

fun Collection<Double>.geometricMean() =
        if (isEmpty())
            Double.NaN
        else Math.pow(reduce { n1, n2 -> n1 * n2 }, 1.0 / size)

fun Collection<Double>.harmonicMean() =
        if (isEmpty() || contains(0.0))
            Double.NaN
        else
            size / reduce { n1, n2 -> n1 + 1.0 / n2 }

fun main(args: Array<String>) {
    val list = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
    val a = list.average()  // arithmetic mean
    val g = list.geometricMean()
    val h = list.harmonicMean()
    println("A = %f  G = %f  H = %f".format(a, g, h))
    println("A >= G is %b, G >= H is %b".format( a >= g, g >= h))
    require(a >= g && g >= h)
}