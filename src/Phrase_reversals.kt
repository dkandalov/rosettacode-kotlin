package `phrase_reversals`

// version 1.0.6

fun reverseEachWord(s: String) = s.split(" ").map { it.reversed() }.joinToString(" ")

fun main(args: Array<String>) {
    val original = "rosetta code phrase reversal"
    val reversed = original.reversed()
    println("Original string => $original")
    println("Reversed string => $reversed")
    println("Reversed words  => ${reverseEachWord(original)}")
    println("Reversed order  => ${reverseEachWord(reversed)}")
}