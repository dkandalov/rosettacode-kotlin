package `knuths_algorithm_s`

// version 1.1.2

import java.util.Random

class SOfN<T>(val n: Int) {
    private val sample = ArrayList<T>(n)
    private var i = 0

    private companion object {
        val rand = Random()
    }

    fun process(item: T): ArrayList<T> {
        if (++i <= n)
            sample.add(item)
        else if (rand.nextInt(i) < n)
            sample[rand.nextInt(n)] = item
        return sample
    }
}

fun main(args: Array<String>) {
    val bin = IntArray(10)
    (1..100_000).forEach {
        val sOfn = SOfN<Int>(3)
        var sample: ArrayList<Int>? = null
        for (i in 0..9) sample = sOfn.process(i)
        for (s in sample!!) bin[s]++
    }
    println(bin.contentToString())
}