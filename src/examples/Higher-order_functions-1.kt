package `higher_order_functions_1`

inline fun higherOrderFunction(x: Int, y: Int, function: (Int, Int) -> Int) = function(x, y)

fun main(args: Array<String>) {
    val result = higherOrderFunction(3, 5) { x, y -> x + y }
    println(result)
}