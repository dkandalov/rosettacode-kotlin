package `string_length`

// version 1.0.6
fun main(args: Array<String>) {
    val s = "José"
    println("The char length is ${s.length}")
    println("The byte length is ${Character.BYTES * s.length}")
}