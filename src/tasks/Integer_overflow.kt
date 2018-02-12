package `integer_overflow`

// version 1.0.5-2

/*  Kotlin (like Java) does not have unsigned integer types but we can simulate
    what would happen if we did have an unsigned 32 bit integer type using this extension function */
fun Long.toUInt(): Long = this and 0xffffffffL

@Suppress("INTEGER_OVERFLOW")
fun main(args: Array<String>) {
    // The following 'signed' computations all produce compiler warnings that they will lead to an overflow
    // which have been ignored
    println("*** Signed 32 bit integers ***\n")
    println(-(-2147483647 - 1))
    println(2000000000 + 2000000000)
    println(-2147483647 - 2147483647)
    println(46341 * 46341)
    println((-2147483647 - 1) / -1)
    println("\n*** Signed 64 bit integers ***\n")
    println(-(-9223372036854775807 - 1))
    println(5000000000000000000 + 5000000000000000000)
    println(-9223372036854775807 - 9223372036854775807)
    println(3037000500 * 3037000500)
    println((-9223372036854775807 - 1) / -1)
    // Simulated unsigned computations, no overflow warnings as we're using the Long type
    println("\n*** Unsigned 32 bit integers ***\n")
    println((-4294967295L).toUInt())
    println((3000000000L.toUInt() + 3000000000L.toUInt()).toUInt())
    println((2147483647L - 4294967295L.toUInt()).toUInt())
    println((65537L * 65537L).toUInt())
}