package `generate_lower_case_ascii_alphabet`

// version 1.0.6

fun main(args: Array<String>) {
    val alphabet = CharArray(26) { (it + 97).toChar() }.joinToString("")
    println(alphabet)
}