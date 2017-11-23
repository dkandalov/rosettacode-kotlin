package `polynomial_regression`

// version 1.1.51

fun polyRegression(x: IntArray, y: IntArray) {
    val n = x.size
    val r = 0 until n
    val xm = x.average()
    val ym = y.average()
    val x2m = r.map { it * it }.average()
    val x3m = r.map { it * it * it }.average()
    val x4m = r.map { it * it * it * it }.average()
    val xym = x.zip(y).map { it.first * it.second }.average()
    val x2ym = x.zip(y).map { it.first * it.first * it.second }.average()

    val sxx = x2m - xm * xm
    val sxy = xym - xm * ym
    val sxx2 = x3m - xm * x2m
    val sx2x2 = x4m - x2m * x2m
    val sx2y = x2ym - x2m * ym

    val b = (sxy * sx2x2 - sx2y * sxx2) / (sxx * sx2x2 - sxx2 * sxx2)
    val c = (sx2y * sxx - sxy * sxx2) / (sxx * sx2x2 - sxx2 * sxx2)
    val a = ym - b * xm - c * x2m

    fun abc(xx: Int) = a + b * xx + c * xx * xx

    println("y = $a + ${b}x + ${c}x^2\n")
    println(" Input  Approximation")
    println(" x   y     y1")
    for (i in 0 until n) {
        System.out.printf("%2d %3d  %5.1f\n", x[i], y[i], abc(x[i]))
    }
}

fun main(args: Array<String>) {
    val x = IntArray(11) { it }
    val y = intArrayOf(1, 6, 17, 34, 57, 86, 121, 162, 209, 262, 321)
    polyRegression(x, y)
}