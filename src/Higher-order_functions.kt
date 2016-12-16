package `higher_order_functions`

fun main(args: Array<String>) {
    val list = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
    var a = list.map({ x -> x + 2 }).average()
    var h = list.map({ x -> x * x }).average()
    var g = list.map({ x -> x * x * x }).average()
    println("A = %f  G = %f  H = %f".format(a, g, h))
}