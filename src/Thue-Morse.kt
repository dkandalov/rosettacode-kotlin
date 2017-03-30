package `thue_morse`

// version 1.1.1
fun thueMorse(n: Int): String {
    val sb0 = StringBuilder("0")
    val sb1 = StringBuilder("1")
    (0 until n).forEach {
        val tmp = sb0.toString()
        sb0.append(sb1)
        sb1.append(tmp)
    }
    return sb0.toString()
}

fun main(args: Array<String>) {
    for (i in 0..6) println("$i : ${thueMorse(i)}")
}