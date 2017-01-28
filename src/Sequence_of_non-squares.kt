package `sequence_of_non_squares`

// version 1.0.6

fun f(n: Int) = n + Math.floor(0.5 + Math.sqrt(n.toDouble())).toInt()

fun main(args: Array<String>) {
    println(" n   f")
    var squares = mutableListOf<Int>()
    var v1: Int
    var v2: Int
    for (n in 1 until 1000000) {
        v1 = f(n)
        v2 = Math.sqrt(v1.toDouble()).toInt()
        if (v1 == v2 * v2) squares.add(n)
        if (n < 23) println("${"%2d".format(n)} : $v1")
    }
    println() 
    if (squares.size == 0) println("There are no squares for n less than one million")
    else println("Squares are generated for the following values of n: $squares") 
}