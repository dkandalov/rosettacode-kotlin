package fibonacci

enum class Fibonacci {
    ITERATIVE {
        override fun invoke(n: Long) = if (n < 2 )
            n
        else {
            var n1: Long = 0
            var n2: Long = 1
            var i = n
            do {
                val sum = n1 + n2
                n1 = n2
                n2 = sum
            } while (i-- > 1)
            n1
        }
    },
    RECURSIVE {
        override fun invoke(n: Long): Long = if (n < 2) n else this(n - 1) + this(n - 2)
    };

    abstract operator fun invoke(n: Long): Long
}

fun main(a: Array<String>) {
    val r = 0..30L
    Fibonacci.values().forEach {
        print("${it.name}: ")
        r.forEach { i -> print(" " + it(i)) }
        println()
    }
}