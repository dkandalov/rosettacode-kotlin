package `closures_value_capture`

// version 1.0.6

fun main(args: Array<String>) {
    // create an array of 10 anonymous functions which return the square of their index
    val funcs = Array(10){ fun(): Int = it * it }
    // call all but the last
    (0 .. 8).forEach { println(funcs[it]()) } 
}