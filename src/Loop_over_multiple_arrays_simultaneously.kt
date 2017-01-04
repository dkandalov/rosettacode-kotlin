package `loop_over_multiple_arrays_simultaneously`

// version 1.0.6

fun main(args: Array<String>) {
    val a1 = charArrayOf('a', 'b', 'c')
    val a2 = charArrayOf('A', 'B', 'C')
    val a3 = intArrayOf(1, 2, 3) 
    for(i in 0 .. 2) println("${a1[i]}${a2[i]}${a3[i]}")
    println()
    // For arrays of different sizes we would need to iterate up to the mimimm size of all 3 in order
    // to  get a contribution from each one.
    val a4 = intArrayOf(4, 5, 6, 7)
    val a5 = charArrayOf('d', 'e')
    val minSize = Math.min(a2.size, Math.min(a4.size, a5.size))  // minimum size of a2, a4 and a5
    for(i in 0 until minSize) println("${a2[i]}${a4[i]}${a5[i]}")
}