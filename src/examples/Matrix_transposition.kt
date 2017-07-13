package `matrix_transposition`

// version 1.1.3

typealias Vector = DoubleArray
typealias Matrix = Array<Vector>

fun Matrix.transpose(): Matrix {
    val rows = this.size
    val cols = this[0].size
    val trans = Matrix(cols) { Vector(rows) }
    for (i in 0 until cols) {
        for (j in 0 until rows) trans[i][j] = this[j][i]
    }
    return trans
}

fun printMatrix(m: Matrix) {
    for (i in 0 until m.size) println(m[i].contentToString())
}

fun main(args: Array<String>) {
    val m = arrayOf(
        doubleArrayOf( 1.0,  2.0,  3.0),
        doubleArrayOf( 4.0,  5.0,  6.0),
        doubleArrayOf( 7.0,  8.0,  9.0),
        doubleArrayOf(10.0, 11.0, 12.0)
    )
    printMatrix(m.transpose())
}