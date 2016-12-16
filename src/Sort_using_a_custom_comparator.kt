package `sort_using_a_custom_comparator`

import java.util.Arrays

fun main(args: Array<String>) {
    val strings = arrayOf("Here", "are", "some", "sample", "strings", "to", "be", "sorted")

    fun printArray(message: String, array: Array<String>) = with(array) {
        print("$message [")
        forEachIndexed { index, string ->
            print(if (index == lastIndex) string else "$string, ")
        }
        println("]")
    }

    printArray("Unsorted:", strings)

    Arrays.sort(strings) { first, second ->
        val lengthDifference = second.length - first.length
        if (lengthDifference == 0) first.compareTo(second, true) else lengthDifference
    }

    printArray("Sorted:", strings)
}