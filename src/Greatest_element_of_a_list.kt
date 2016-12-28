package `greatest_element_of_a_list`

// version 1.0.5-2
fun main(args: Array<String>) {
    print("Number of values to be input = ")
    val n = readLine()!!.toInt()
    val array = DoubleArray(n)
    for (i in 0 until n) {
         print("Value ${i + 1} = ")
         array[i] = readLine()!!.toDouble()
    }
    println("\nThe greatest element is ${array.max()}")
}