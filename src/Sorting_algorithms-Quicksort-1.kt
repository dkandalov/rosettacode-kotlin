package `sorting_algorithms_quicksort_1`

fun quicksort(list: List<Int>): List<Int> {
    if (list.size == 0) {
        return listOf()
    } else {
        val head = list.first()
        val tail = list.takeLast(list.size - 1)

        val less = quicksort(tail.filter { it < head })
        val high = quicksort(tail.filter { it >= head })

        return less + head + high
    }
}

fun main(args: Array<String>) {
    val nums = listOf(9, 7, 9, 8, 1, 2, 3, 4, 1, 9, 8, 9, 2, 4, 2, 4, 6, 3)
    println(quicksort(nums))
}