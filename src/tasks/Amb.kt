package `amb`

// version 1.1.3

typealias Constraint<T> = (T, T) -> Boolean

fun <T> amb(lists: List<List<T>>, res: MutableList<T>, cons: Constraint<T>): Boolean {
    if (lists.isEmpty()) return true
    if (lists[0].isEmpty()) return false
    val z = res.size
    for ((i, el) in lists[0].withIndex()) {
        if (i == 0) res.add(el) else res[z] = el
        if (z > 0 && !cons(res[z - 1], res[z])) continue
        if (amb(lists.drop(1), res, cons)) return true
    }
    res.removeAt(z)
    return false
}

fun main(args: Array<String>) {
    val wordLists = listOf(
        listOf("the", "that", "a"),
        listOf("frog", "elephant", "thing"),
        listOf("walked", "treaded", "grows"),
        listOf("slowly", "quickly")
    )
    val res = mutableListOf<String>()
    val cons: Constraint<String> = { x, y -> x.last() == y.first() }
    if (amb(wordLists, res, cons))
        println(res)
    else
        println("No solution found")
    val numLists = listOf(
        listOf(1, 2, 3),
        listOf(7, 6, 4, 5)
    )
    val res2 = mutableListOf<Int>()
    val cons2: Constraint<Int> = { x, y -> x * y == 8 }
    if (amb(numLists, res2, cons2))
        println(res2)
    else
        println("No solution found")
}