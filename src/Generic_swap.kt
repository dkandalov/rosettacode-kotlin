package `generic_swap`

// version 1.0.5-2
fun <T> swap(t1: T, t2: T) = Pair<T, T>(t2, t1)

fun main(args: Array<String>) {
    var a = 3
    var b = 4
    val c = swap(a, b) // infers that swap<Int> be used
    a = c.first
    b = c.second 
    println("a = $a")
    println("b = $b")
    var d = false
    var e = true
    val f = swap(d, e) // infers that swap<Boolean> be used
    d = f.first
    e = f.second
    println("d = $d")
    println("e = $e")
}