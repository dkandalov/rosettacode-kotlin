package `sorting_algorithms_pancake_sort`

// version 1.1.2

class PancakeSort(private val a: IntArray) {
    init {
        for (n in a.size downTo 2) {  // successively reduce size of array by 1
            val index = indexOfMax(n) // find index of largest
            if (index != n - 1) {     // if it's not already at the end
                if (index > 0) {      // if it's not already at the beginning
                    flip(index)       // move largest to beginning
                    println("${a.contentToString()} after flipping first ${index + 1}")
                }
                flip(n - 1)           // move largest to end
                println("${a.contentToString()} after flipping first $n")
            }
        }
    }

    private fun indexOfMax(n: Int): Int {
        var index = 0
        for (i in 1 until n) {
            if (a[i] > a[index]) index = i
        }
        return index
    }

    private fun flip(index: Int) {
        var i = index
        var j = 0
        while (j < i) {
            val temp = a[j]
            a[j] = a[i]
            a[i] = temp
            j++
            i--
        }
    }
}

fun main(args: Array<String>) {
    val a = intArrayOf(7, 6, 9, 2, 4, 8, 1, 3, 5)
    println("${a.contentToString()} initially")
    PancakeSort(a)
}