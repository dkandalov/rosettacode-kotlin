package `currying`

// version 1.1.1

fun curriedAdd(x: Int) = { y: Int -> x + y }

fun main(args: Array<String>) {
    val a = 2
    val b = 3
    val sum = curriedAdd(a)(b)
    println("$a + $b = $sum")
}