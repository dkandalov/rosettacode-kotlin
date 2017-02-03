package `pick_random_element`

// version 1.0.6

fun main(args: Array<String>) {
    val list = listOf(1, 16, 3, 7, 17, 24, 34, 23, 11, 2)
    println("The list consists of the following numbers:")
    println(list)
    val chosen = mutableSetOf<Int>()
    println("\nFive elements chosen at random without duplication are:")
    while (chosen.size < 5) {
        val r = list[(Math.random() * list.size).toInt()] 
        if (chosen.add(r)) println(r)
    }
}