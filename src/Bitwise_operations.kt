package `bitwise_operations`

// version 1.0.5-2

/*  for symmetry with Kotlin's other binary bitwise operators
    we wrap Java's 'rotate' methods as infix functions */
infix fun Int.rol(distance: Int): Int = Integer.rotateLeft(this, distance)
infix fun Int.ror(distance: Int): Int = Integer.rotateRight(this, distance)

fun main(args: Array<String>) {
    // inferred type of x and y is Int i.e. 32 bit signed integers
    val x = 10
    val y = 2
    println("x       = $x")
    println("y       = $y")
    println("NOT x   = ${x.inv()}")
    println("x AND y = ${x and y}")
    println("x OR  y = ${x or y}")
    println("x XOR y = ${x xor y}")    
    println("x SHL y = ${x shl y}")
    println("x ASR y = ${x shr y}")   // arithmetic shift right (sign bit filled)
    println("x LSR y = ${x ushr y}")  // logical shift right    (zero filled) 
    println("x ROL y = ${x rol y}")
    println("x ROR y = ${x ror y}")
}