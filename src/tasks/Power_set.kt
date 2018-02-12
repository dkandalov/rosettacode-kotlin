package `power_set`

// version 1.1.3

class PowerSet<T>(val items: List<T>) {
    private lateinit var combination: IntArray

    init {
        println("Power set of $items comprises:")
        for (m in 0..items.size) {
            combination = IntArray(m)
            generate(0, m)
        }
    }

    private fun generate(k: Int, m: Int) {
        if (k >= m) {
            println(combination.map { items[it] })
        }
        else {
            for (j in 0 until items.size)
                if (k == 0 || j > combination[k - 1]) {
                    combination[k] = j
                    generate(k + 1, m)
                }
        }
    }
}

fun main(args: Array<String>) {
    val itemsList = listOf(
        listOf(1, 2, 3, 4),
        emptyList<Int>(),
        listOf(emptyList<Int>())
    )
    for (items in itemsList) {
        PowerSet(items)
        println()
    }
}