package `array_concatenation`

fun main(args: Array<String>) {
    val a: Array<Int> = arrayOf(1, 2, 3) // initialise a
    val b: Array<Int> = arrayOf(4, 5, 6) // initialise b
    val c: Array<Int> = (a.toList() + b.toList()).toTypedArray()
    println(c)
}