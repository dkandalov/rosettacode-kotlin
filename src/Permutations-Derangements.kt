package `permutations_derangements`

// version 1.1.1

fun <T> permute(input: List<T>): List<List<T>> {
    if (input.size == 1) return listOf(input)
    val perms = mutableListOf<List<T>>()
    val toInsert = input[0]
    for (perm in permute(input.drop(1))) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}

fun derange(input: List<Int>): List<List<Int>> {
    if (input.size == 0) return listOf(input)
    val deras = mutableListOf<List<Int>>()
    for (perm in permute(input)) {
        var isDera = true
        for ((i, index) in perm.withIndex()) {
            if (i == index) {
                isDera = false
                break
            }
        }
        if (isDera) deras.add(perm)
    }
    return deras
}

fun subfactorial(n: Int): Long =
    when (n) {
        0 -> 1
        1 -> 0
        else -> (n - 1) * (subfactorial(n - 1) + subfactorial(n - 2))
    }

fun main(args: Array<String>) {
    val input = listOf(0, 1, 2, 3)
    val deras = derange(input)
    println("There are ${deras.size} derangements of $input, namely:\n")
    for (dera in deras) println(dera)
    println("\nN  Counted   Calculated")
    println("-  -------   ----------")
    for (n in 0..9) {
        val list = List(n) { it }
        val counted = derange(list).size
        println("%d  %-9d %-9d".format(n, counted, subfactorial(n)))
    }
    println("\n!20 = ${subfactorial(20)}")
}