package `compound_data_type`

// version 1.0.5-2

data class Point(var x: Int, var y: Int)

fun main(args: Array<String>) {
    val p = Point(1, 2)
    println(p)
    p.x = 3
    p.y = 4
    println(p)
}